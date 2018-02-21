/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.gavlyukovskiy.boot.jdbc.decorator.p6spy;

import com.github.gavlyukovskiy.boot.jdbc.decorator.DataSourceDecorator;
import com.p6spy.engine.spy.JdbcEventListenerFactory;
import com.p6spy.engine.spy.P6DataSource;
import org.springframework.core.Ordered;

import javax.sql.DataSource;

/**
 * {@link Ordered} decorator for {@link P6DataSource}.
 *
 * @author Arthur Gavlyukovskiy
 */
public class P6SpyDataSourceDecorator implements DataSourceDecorator, Ordered {

    private final JdbcEventListenerFactory jdbcEventListenerFactory;

    P6SpyDataSourceDecorator(JdbcEventListenerFactory jdbcEventListenerFactory) {
        this.jdbcEventListenerFactory = jdbcEventListenerFactory;
    }

    @Override
    public DataSource decorate(String beanName, DataSource dataSource) {
        P6DataSource p6DataSource = new P6DataSource(dataSource);
        p6DataSource.setJdbcEventListenerFactory(jdbcEventListenerFactory);
        return p6DataSource;
    }

    @Override
    public int getOrder() {
        return 30;
    }
}
