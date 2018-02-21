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
package com.mengka.cloud.flexypoolsample;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 》》【flexy-pool Druid Configuration】：
 *  https://github.com/vladmihalcea/flexy-pool/wiki/Druid-Configuration
 *
 * 》》【github代码】：
 * https://github.com/vladmihalcea/flexy-pool
 *
 * 》》【flexy-pool-spring-boot-starter】：
 * https://github.com/gavlyukovskiy/spring-boot-data-source-decorator
 *
 * 》》【分布式调用追踪】：
 * Distributed tracing for spring cloud http://cloud.spring.io
 *
 * https://github.com/spring-cloud/spring-cloud-sleuth
 *
 */
@Slf4j
@RestController
@RequestMapping
public class SampleController {

    private final DataSource dataSource;

    public SampleController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @RequestMapping("/commit")
    public List<Map<String, String>> select() {
        List<Map<String, String>> results = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS")) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, String> result = new HashMap<>();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnName(i + 1);
                    result.put(columnName, resultSet.getString(columnName));
                }
                results.add(result);
            }
            connection.commit();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return results;
    }

    @RequestMapping("/rollback")
    public List<Map<String, String>> rollback() {
        List<Map<String, String>> results = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS")) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, String> result = new HashMap<>();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnName(i + 1);
                    result.put(columnName, resultSet.getString(columnName));
                }
                results.add(result);
            }
            connection.rollback();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return results;
    }

    @RequestMapping("/query-error")
    public void error() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("SELECT UNDEFINED()");
        } catch (Exception ignored) {
        }
    }

    @RequestMapping("/t1")
    public void t1() {
        createTable(SQLConstant.MQ_TABLE_NAME, SQLConstant.MQ_ATTRIBUTE_ID, SQLConstant.MQ_ATTRIBUTE_CONTENT, SQLConstant.MQ_ATTRIBUTE_TAGS, SQLConstant.MQ_ATTRIBUTE_KEYS, SQLConstant.MQ_ATTRIBUTE_STATUS);
    }

    private void createTable(String tableName, String... attributes) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            if (attributes.length <= 0) {
                log.error("attributes is empty!");
                return;
            }
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < attributes.length; i++) {
                if (i == attributes.length - 1) {
                    buffer.append(attributes[i] + " VARCHAR(255)");
                } else {
                    buffer.append(attributes[i] + " VARCHAR(255),");
                }
            }
            String sql = String.format(SQLConstant.sql_create_table, tableName, buffer.toString());
            statement.executeUpdate(sql);
        } catch (Exception e) {
            log.error("createTable error!", e);
        }
    }

    @RequestMapping("/t2")
    public void t2() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            String msgId = "123456";
            String body = "Just for Test[..";
            String tag = "";
            String keys = "hz,xihu";

            //save message
            String sql = String.format(SQLConstant.sql_insert_table_message, SQLConstant.MQ_TABLE_NAME, msgId, body, tag, keys, 0);
            statement.executeUpdate(sql);

            MessageExt existMessageExt = getMessageExtById(msgId, statement);
            log.info("---------, existMessageExt = " + JSON.toJSONString(existMessageExt));

        } catch (Exception e) {
            log.error("createTable error!", e);
        }
    }

    private MessageExt getMessageExtById(String id, Statement statement) throws Exception {
        try {
            String sql = "SELECT * FROM mq_message where id='" + id + "';";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                MessageExt messageExt = new MessageExt();
                messageExt.setMsgId(rs.getString(SQLConstant.MQ_ATTRIBUTE_ID));
                messageExt.setBody(rs.getString(SQLConstant.MQ_ATTRIBUTE_CONTENT).getBytes());
                messageExt.setTags(rs.getString(SQLConstant.MQ_ATTRIBUTE_TAGS));
                messageExt.setKeys(rs.getString(SQLConstant.MQ_ATTRIBUTE_KEYS));
                return messageExt;
            }
        } catch (Exception e) {
            log.error("getMessageExts error!", e);
        }
        return null;
    }
}
