package com.atguigu.study.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * PostgreSQL 数据源配置（用于 pgvector 向量库 + 明星信息表）
 */
@Configuration
@MapperScan(
        basePackages = "com.atguigu.study.pgmapper",
        sqlSessionFactoryRef = "pgSqlSessionFactory"
)
public class PgVectorDataSourceConfig {

    @Bean(name = "pgDataSource")
    @Primary
    @ConfigurationProperties(prefix = "pg.datasource")
    public DataSource pgDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "pgSqlSessionFactory")
    public SqlSessionFactory pgSqlSessionFactory(@Qualifier("pgDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:pgmapper/**/*.xml"));
        bean.setTypeAliasesPackage("com.atguigu.study.domain");
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        bean.setConfiguration(configuration);
        return bean.getObject();
    }

    @Bean(name = "pgTransactionManager")
    public DataSourceTransactionManager pgTransactionManager(@Qualifier("pgDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "pgSqlSessionTemplate")
    public SqlSessionTemplate pgSqlSessionTemplate(@Qualifier("pgSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}