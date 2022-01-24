/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2015, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.data.databricks;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.geotools.data.Parameter;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.geotools.jdbc.SQLDialect;

/**
 * DataStoreFactory for Databricks Lakehouse.
 *
 * @author Patrick Bayne <patrickbayne@databricks.com>
 */
public class DatabricksDataStoreFactory extends JDBCDataStoreFactory {
    /** parameter for database type */
    public static final Param DBTYPE =
            new Param(
                    "dbtype",
                    String.class,
                    "Type",
                    true,
                    "spark",
                    Collections.singletonMap(Parameter.LEVEL, "program"));

    /** Defaults for Databricks JDBC */
    public static final Param CONN_STRING =
            new Param(
                    "Connection String",
                    String.class,
                    "Connection String as found in your Databricks compute instance properties",
                    true,
                    "");

    private static final String DRIVER_CLASS_NAME = "com.simba.spark.jdbc.Driver";

    @Override
    protected SQLDialect createSQLDialect(JDBCDataStore dataStore) {
        return new DatabricksDialect(dataStore);
    }

    @Override
    public String getDisplayName() {
        return "Databricks Lakehouse";
    }

    @Override
    protected String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }

    @Override
    protected String getDatabaseID() {
        return (String) DBTYPE.sample;
    }

    @Override
    public String getDescription() {
        return "Connecting to a Databricks Lakehouse cloud store";
    }

    @Override
    protected String getValidationQuery() {
        return "select now()";
    }

    @Override
    protected void setupParameters(Map<String, Object> parameters) {
        super.setupParameters(parameters);
        parameters.put(CONN_STRING.key, CONN_STRING);
        // Remove default Paramenters we dont care about
        parameters.remove(HOST.key, HOST);
        parameters.remove(PORT.key, PORT);
        parameters.remove(DATABASE.key, DATABASE);
        parameters.remove(SCHEMA.key, SCHEMA);
        parameters.remove(USER.key, USER);
        parameters.remove(PASSWD.key, PASSWD);
        parameters.remove(NAMESPACE.key, NAMESPACE);
        parameters.remove(EXPOSE_PK.key, EXPOSE_PK);
        parameters.remove(MAXCONN.key, MAXCONN);
        parameters.remove(MINCONN.key, MINCONN);
        parameters.remove(FETCHSIZE.key, FETCHSIZE);
        parameters.remove(BATCH_INSERT_SIZE.key, BATCH_INSERT_SIZE);
        parameters.remove(MAXWAIT.key, MAXWAIT);
        if (getValidationQuery() != null) parameters.put(VALIDATECONN.key, VALIDATECONN);
        parameters.remove(TEST_WHILE_IDLE.key, TEST_WHILE_IDLE);
        parameters.remove(TIME_BETWEEN_EVICTOR_RUNS.key, TIME_BETWEEN_EVICTOR_RUNS);
        parameters.remove(MIN_EVICTABLE_TIME.key, MIN_EVICTABLE_TIME);
        parameters.remove(EVICTOR_TESTS_PER_RUN.key, EVICTOR_TESTS_PER_RUN);
        parameters.remove(PK_METADATA_TABLE.key, PK_METADATA_TABLE);
        parameters.remove(SQL_ON_BORROW.key, SQL_ON_BORROW);
        parameters.remove(SQL_ON_RELEASE.key, SQL_ON_RELEASE);
        parameters.remove(CALLBACK_FACTORY.key, CALLBACK_FACTORY);
    }

    @Override
    protected String getJDBCUrl(Map<String, ?> params) throws IOException {
        return ((String) CONN_STRING.lookUp(params));
    }

    @Override
    protected JDBCDataStore createDataStoreInternal(JDBCDataStore dataStore, Map<String, ?> params)
            throws IOException {
        throw new UnsupportedOperationException(
                "Databricks Lakehouse not allowed in embedded mode");
    }
}
