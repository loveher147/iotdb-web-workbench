package org.apache.iotdb.admin.service.impl;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.model.dto.*;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.*;
import org.apache.iotdb.admin.service.IotDBService;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionDataSetWrapper;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.tsfile.file.metadata.enums.CompressionType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
import org.apache.iotdb.tsfile.read.common.RowRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.Date;
import java.util.*;


@Service
public class IotDBServiceImpl implements IotDBService {

    private static final Logger logger = LoggerFactory.getLogger(IotDBServiceImpl.class);

    private static final HashMap<String,Boolean> SPECIAL_PRIVILEGES = new HashMap();

    private static final String NO_NEED_PRIVILEGES = "SET_STORAGE_GROUP";

    private static final List<String> PRIVILEGES = new ArrayList<>();

    static {
        SPECIAL_PRIVILEGES.put("CREATE_TIMESERIES",true);
        SPECIAL_PRIVILEGES.put("INSERT_TIMESERIES",true);
        SPECIAL_PRIVILEGES.put("READ_TIMESERIES",true);
        SPECIAL_PRIVILEGES.put("DELETE_TIMESERIES",true);
    }

    static {
        PRIVILEGES.add("SET_STORAGE_GROUP");
        PRIVILEGES.add("CREATE_TIMESERIES");
        PRIVILEGES.add("INSERT_TIMESERIES");
        PRIVILEGES.add("READ_TIMESERIES");
        PRIVILEGES.add("DELETE_TIMESERIES");
        PRIVILEGES.add("CREATE_USER");
        PRIVILEGES.add("DELETE_USER");
        PRIVILEGES.add("MODIFY_PASSWORD");
        PRIVILEGES.add("LIST_USER");
        PRIVILEGES.add("GRANT_USER_PRIVILEGE");
        PRIVILEGES.add("REVOKE_USER_PRIVILEGE");
        PRIVILEGES.add("CREATE_FUNCTION");
        PRIVILEGES.add("DROP_FUNCTION");
        PRIVILEGES.add("CREATE_TRIGGER");
        PRIVILEGES.add("DROP_TRIGGER");
        PRIVILEGES.add("START_TRIGGER");
        PRIVILEGES.add("STOP_TRIGGER");
    }


    @Override
    public List<String> getAllStorageGroups(Connection connection) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "show storage group";
        List<String> users = executeQueryOneColumn(sessionPool, sql);
        sessionPool.close();
        return users;
    }

    @Override
    public void saveStorageGroup(Connection connection, String groupName) throws BaseException {
        paramValid(groupName);
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "set storage group to " + groupName;
        try {
            sessionPool.executeNonQueryStatement(sql);
        } catch (StatementExecutionException e) {
            // 捕获异常代表存储组已有
            logger.error(e.getMessage());
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
        } finally {
            if (sessionPool != null) {
                sessionPool.close();
            }
        }
    }

    @Override
    public void deleteStorageGroup(Connection connection, String groupName) throws BaseException {
        paramValid(groupName);
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "delete storage group " + groupName;
        try {
            sessionPool.executeNonQueryStatement(sql);
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.DELETE_GROUP_FAIL,ErrorCode.DELETE_GROUP_FAIL_MSG);
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.DELETE_GROUP_FAIL,ErrorCode.DELETE_GROUP_FAIL_MSG);
        } finally {
            if (sessionPool != null) {
                sessionPool.close();
            }
        }
    }

    @Override
    public List<String> getDevicesByGroup(Connection connection, String groupName,Integer pageSize,Integer pageNum) throws BaseException {
        paramValid(groupName);
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "show devices " + groupName;
        List<String> devices = executeQueryOneColumn(sessionPool, sql, pageSize, pageNum);
        sessionPool.close();
        return devices;
    }

    @Override
    public List<MeasurementDTO> getMeasurementsByDevice(Connection connection, String deviceName, Integer pageSize, Integer pageNum) throws BaseException {
        paramValid(deviceName);
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "show timeseries " + deviceName;
        List<MeasurementDTO> measurementDTOList = executeQuery(MeasurementDTO.class,sessionPool,sql,pageSize,pageNum);
        return measurementDTOList;
        //        java.sql.Connection conn = getConnection(connection);
//        String sql = "show child paths " + deviceName;
//        List<String> measurements = customExecuteQuery(conn, sql);
//        closeConnection(conn);
    }

    @Override
    public List<String> getIotDBUserList(Connection connection) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "list user";
        List<String> users = executeQueryOneColumn(sessionPool, sql);
        sessionPool.close();
        return users;
    }

    @Override
    public List<String> getIotDBRoleList(Connection connection) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "list role";
        List<String> roles = executeQueryOneColumn(sessionPool, sql);
        sessionPool.close();
        return roles;
    }

    @Override
    public IotDBUserVO getIotDBUser(Connection connection, String userName) throws BaseException {
        paramValid(userName);
        IotDBUserVO iotDBUserVO = new IotDBUserVO();
        iotDBUserVO.setUserName(connection.getUsername());
        iotDBUserVO.setPassword(connection.getPassword());
        if ("root".equalsIgnoreCase(userName)) {
            List<PrivilegeInfo> privilegeInfos = new ArrayList<>();
            PrivilegeInfo privilegeInfo = new PrivilegeInfo();
            privilegeInfo.setType(0);
            privilegeInfo.setPrivileges(PRIVILEGES);
            privilegeInfos.add(privilegeInfo);
            iotDBUserVO.setPrivilegesInfo(privilegeInfos);
            return iotDBUserVO;
        }
        SessionPool sessionpool = getSessionPool(connection);
        String sql = "list user privileges " + userName;
        try {
            SessionDataSetWrapper sessionDataSetWrapper =  sessionpool.executeQueryStatement(sql);
            int batchSize = sessionDataSetWrapper.getBatchSize();
            if (batchSize  > 0) {
                List<String> privileges = new ArrayList<>();
                while (sessionDataSetWrapper.hasNext()) {
                    RowRecord next = sessionDataSetWrapper.next();
                    List<org.apache.iotdb.tsfile.read.common.Field> fields = next.getFields();
                    for (int i = 0; i < fields.size(); i++) {
                        org.apache.iotdb.tsfile.read.common.Field field = fields.get(i);
                        if (i == 0) {
                            if (field != null && field.toString().length() > 0) {
                                break;
                            }
                            continue;
                        }
                        privileges.add(field.toString());
                    }
                }
                // privileges String内容形式 "path : 权限1 权限2 权限3"
                // 组装成权限信息集合
                List<PrivilegeInfo> privilegeInfos = privilegesStrSwitchToObject(sessionpool,privileges);
                iotDBUserVO.setPrivilegesInfo(privilegeInfos);
            }
        }catch (Exception e) {
            throw new BaseException(ErrorCode.GET_USER_FAIL,ErrorCode.GET_USER_FAIL_MSG);
        } finally {
            if (sessionpool != null) {
                sessionpool.close();
            }
        }
        return iotDBUserVO;
    }

    @Override
    public void deleteIotDBUser(Connection connection, String userName) throws BaseException {
        paramValid(userName);
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "drop user " + userName;
        try {
            sessionPool.executeNonQueryStatement(sql);
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.DELETE_DB_USER_FAIL,ErrorCode.DELETE_DB_USER_FAIL_MSG);
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.DELETE_DB_USER_FAIL,ErrorCode.DELETE_DB_USER_FAIL_MSG);
        } finally {
            if (sessionPool != null) {
                sessionPool.close();
            }
        }
    }

    @Override
    public void deleteIotDBRole(Connection connection, String roleName) throws BaseException {
        paramValid(roleName);
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "drop role " + roleName;
        try {
            sessionPool.executeNonQueryStatement(sql);
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.DELETE_DB_ROLE_FAIL,ErrorCode.DELETE_DB_ROLE_FAIL_MSG);
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.DELETE_DB_ROLE_FAIL,ErrorCode.DELETE_DB_ROLE_FAIL_MSG);
        } finally {
            if (sessionPool != null) {
                sessionPool.close();
            }
        }
    }

    @Override
    public void setIotDBUser(Connection connection, IotDBUser iotDBUser) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        String userName = iotDBUser.getUserName();
        String password = iotDBUser.getPassword();
        String sql = "create user " + userName + " '" + password + "'";
        try {
            sessionPool.executeNonQueryStatement(sql);
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.SET_DB_USER_FAIL,ErrorCode.SET_DB_USER_FAIL_MSG);
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.SET_DB_USER_FAIL,ErrorCode.SET_DB_USER_FAIL_MSG);
        } finally {
            if (sessionPool != null) {
                sessionPool.close();
            }
        }
//        // 用户角色
//        for (String role : iotDBUser.getRoles()) {
//            paramValid(role);
//            sql = "grant " + role + " to " + userName;
//            customExecute(conn, sql);
//        }
//        // 用户授权
//        List<String> privileges = iotDBUser.getPrivileges();
//        for (String privilege : privileges) {
//            sql = handlerPrivilegeStrToSql(privilege, userName, null);
//            if (sql != null) {
//                customExecute(conn, sql);
//            }
//        }
    }

    @Override
    public void setIotDBRole(Connection connection, IotDBRole iotDBRole) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        String roleName = iotDBRole.getRoleName();
        String sql = "create role " + roleName;
        try {
            sessionPool.executeNonQueryStatement(sql);
            List<String> privileges = iotDBRole.getPrivileges();
            for (String privilege : privileges) {
                sql = handlerPrivilegeStrToSql(privilege, null, roleName);
                if (sql != null) {
                    sessionPool.executeNonQueryStatement(sql);
                }
            }
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.SET_DB_ROLE_FAIL,ErrorCode.SET_DB_ROLE_FAIL_MSG);
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.SET_DB_ROLE_FAIL,ErrorCode.SET_DB_ROLE_FAIL_MSG);
        }finally {
            if (sessionPool != null) {
                sessionPool.close();
            }
        }
    }

    @Override
    public SqlResultVO query(Connection connection, String sql) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        SqlResultVO sqlResultVO = sqlQuery(conn, sql);
        closeConnection(conn);
        return sqlResultVO;
    }

    @Override
    public void insertTimeseries(Connection connection, String deviceName, Timeseries timeseries) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        try {
            List<TSDataType> types = handleTypeStr(timeseries.getTypes());
            List<Object> values = handleValueStr(timeseries.getValues(),types);
            sessionPool.insertRecord(deviceName,timeseries.getTime(),timeseries.getMeasurements(),types,values);
        } catch (IoTDBConnectionException e) {
            throw new BaseException(ErrorCode.INSERT_TS_FAIL, ErrorCode.INSERT_TS_FAIL_MSG);
        } catch (StatementExecutionException e) {
            throw new BaseException(ErrorCode.INSERT_TS_FAIL, ErrorCode.INSERT_TS_FAIL_MSG);
        }finally {
            if (sessionPool != null) {
                sessionPool.close();
            }
        }

    }

    @Override
    public void deleteTimeseries(Connection connection, String timeseriesName) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        try {
            sessionPool.deleteTimeseries(timeseriesName);
        } catch (IoTDBConnectionException e) {
            throw new BaseException(ErrorCode.DELETE_TS_FAIL, ErrorCode.DELETE_TS_FAIL_MSG);
        } catch (StatementExecutionException e) {
            throw new BaseException(ErrorCode.DELETE_TS_FAIL, ErrorCode.DELETE_TS_FAIL_MSG);
        }finally {
            if (sessionPool != null) {
                sessionPool.close();
            }
        }
    }

    @Override
    public SqlResultVO showTimeseries(Connection connection, String deviceName) throws BaseException {
        paramValid(deviceName);
        java.sql.Connection conn = getConnection(connection);
        String sql = "show timeseries " + deviceName;
        SqlResultVO resultVO = sqlQuery(conn, sql);
        closeConnection(conn);
        return resultVO;
    }

    @Override
    public List<Integer> getDevicesCount(Connection connection, List<String> groupNames) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        List<Integer> devicesCount = new ArrayList<>();
        for (String groupName : groupNames) {
            String sql = "count devices " + groupName;
            String value = executeQueryOneValue(sessionPool, sql);
            if (value == null) {
                devicesCount.add(0);
                continue;
            }
            Integer count = Integer.valueOf(value);
            devicesCount.add(count);
        }
        sessionPool.close();
        return devicesCount;
    }

    @Override
    public void saveGroupTtl(Connection connection,String groupName,long l) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "set ttl to " + groupName + " " + l;
        try {
            sessionPool.executeNonQueryStatement(sql);
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.SET_TTL_FAIL,ErrorCode.SET_TTL_FAIL_MSG);
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.SET_TTL_FAIL,ErrorCode.SET_TTL_FAIL_MSG);
        }finally {
            sessionPool.close();
        }
    }

    @Override
    public void cancelGroupTtl(Connection connection, String groupName) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "unset ttl to " + groupName;
        try {
            sessionPool.executeNonQueryStatement(sql);
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.DEL_TTL_FAIL,ErrorCode.DEL_TTL_FAIL_MSG);
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.DEL_TTL_FAIL,ErrorCode.DEL_TTL_FAIL_MSG);
        }finally {
            sessionPool.close();
        }
    }

    @Override
    public Integer getDeviceCount(Connection connection, String groupName) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "count devices " + groupName;
        String value = executeQueryOneValue(sessionPool, sql);
        if (value == null) {
            return 0;
        }
        Integer count = Integer.valueOf(value);
        return count;
    }

    @Override
    public List<Integer> getTimeseriesCount(Connection connection, List<String> deviceNames) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        List<Integer> lines = new ArrayList<>();
        for (String deviceName : deviceNames) {
            String sql = "count timeseries " + deviceName;
            String value = executeQueryOneValue(sessionPool, sql);
            if (value == null) {
                lines.add(0);
                continue;
            }
            Integer count = Integer.valueOf(value);
            lines.add(count);
        }
        sessionPool.close();
        return lines;
    }

    @Override
    public void deleteTimeseriesByDevice(Connection connection, String deviceName) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "delete timeseries " + deviceName + ".*";
        try {
            sessionPool.executeNonQueryStatement(sql);
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.DELETE_TS_FAIL,ErrorCode.DELETE_TS_FAIL_MSG);
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.DELETE_TS_FAIL,ErrorCode.DELETE_TS_FAIL_MSG);
        }
    }

    @Override
    public void createDeviceWithMeasurements(Connection connection, DeviceInfoDTO deviceInfoDTO) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        List<String> typesStr = new ArrayList<>();
        List<String> encodingsStr = new ArrayList<>();
        List<String> measurements = new ArrayList<>();
        for (DeviceDTO deviceDTO : deviceInfoDTO.getDeviceDTOList()) {
            typesStr.add(deviceDTO.getDataType());
            encodingsStr.add(deviceDTO.getEncoding());
            measurements.add(deviceDTO.getTimeseries());
        }
        List<TSDataType> types = handleTypeStr(typesStr);
        List<TSEncoding> encodings = handleEncodingStr(encodingsStr);
        List<CompressionType> compressionTypes = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            compressionTypes.add(CompressionType.SNAPPY);
        }
        try {
            sessionPool.createMultiTimeseries(measurements,types,encodings,compressionTypes,null,null,null,null);
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.INSERT_DEV_FAIL,ErrorCode.INSERT_DEV_FAIL_MSG);
        } catch (StatementExecutionException e) {
            if (!e.getMessage().contains("PathAlreadyExistException")) {
                logger.error(e.getMessage());
                throw new BaseException(ErrorCode.INSERT_DEV_FAIL,ErrorCode.INSERT_DEV_FAIL_MSG);
            }
        }finally {
            if (sessionPool != null) {
                sessionPool.close();
            }
        }
    }

    @Override
    public Integer getMeasurementsCount(Connection connection, String deviceName) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "count timeseries " + deviceName;
        String valueStr = executeQueryOneValue(sessionPool, sql);
        return Integer.valueOf(valueStr);
    }

    @Override
    public String getLastMeasurementValue(Connection connection, String timeseries) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        int index = timeseries.lastIndexOf(".");
        String sql = "select last " + timeseries.substring(index + 1) + " from " + timeseries.substring(0,index);
        try {
            SessionDataSetWrapper sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
            List<String> columnNames = sessionDataSetWrapper.getColumnNames();
            int batchSize = sessionDataSetWrapper.getBatchSize();
            int mark = -1;
            for (int i = 0; i < columnNames.size(); i++) {
                if ("value".equalsIgnoreCase(columnNames.get(i))) {
                    mark = i;
                    break;
                }
            }
            if (mark == -1) {
                throw new BaseException(ErrorCode.NO_SUCH_FIELD,ErrorCode.NO_SUCH_FIELD_MSG);
            }
            if (batchSize > 0) {
                while (sessionDataSetWrapper.hasNext()) {
                    RowRecord rowRecord = sessionDataSetWrapper.next();
                    List<org.apache.iotdb.tsfile.read.common.Field> fields = rowRecord.getFields();
                    // 时间戳不在fields里面 所以下标减1
                    return fields.get(mark - 1).toString();
                }
            }
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.GET_LAST_VALUE_FAIL,ErrorCode.GET_LAST_VALUE_FAIL_MSG);
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.GET_LAST_VALUE_FAIL,ErrorCode.GET_LAST_VALUE_FAIL_MSG);
        } finally {
            if (sessionPool != null) {
                sessionPool.close();
            }
        }
        return null;
    }

    @Override
    public String getGroupTTL(Connection connection, String groupName) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "show ttl on " + groupName;
        String queryField = "ttl";
        String ttl = executeQueryOneLine(sessionPool,sql,queryField);
        return ttl;
    }

    @Override
    public List<String> getDevices(Connection connection, String groupName) throws BaseException {
        paramValid(groupName);
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "show devices " + groupName;
        List<String> devicesName = executeQueryOneColumn(sessionPool, sql);
        return devicesName;
    }

    @Override
    public List<String> getTimeseries(Connection connection, String deviceName) throws BaseException {
        paramValid(deviceName);
        SessionPool sessionPool = getSessionPool(connection);
        String sql = "show timeseries " + deviceName;
        SqlResultVO sqlResultVO = executeQuery(sessionPool, sql,true);
        List<String> metaDataList = sqlResultVO.getMetaDataList();
        int index = -1;
        if (metaDataList != null) {
            for (int i = 0; i < metaDataList.size(); i++) {
                if ("timeseries".equalsIgnoreCase(metaDataList.get(i))) {
                    index = i;
                    break;
                }
            }
        }
        if (index == -1) {
            throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG);
        }
        List<List<String>> valueList = sqlResultVO.getValueList();
        List<String> timeseries = new ArrayList<>();
        for (List<String> list : valueList) {
            timeseries.add(list.get(index));
        }
        return timeseries;
    }

    @Override
    public void setUserPrivileges(Connection connection, String userName,IotDBUserDTO iotDBUserDTO) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        List<PrivilegeInfoDTO> privilegesInfos = iotDBUserDTO.getPrivilegesInfos();
        if (notNullAndNotZero(privilegesInfos)) {
            for (PrivilegeInfoDTO privilegesInfo : privilegesInfos) {
                // 授权
                List<String> privileges = privilegesInfo.getPrivileges();
                if (notNullAndNotZero(privileges)) {
                    grantOrRevoke("grant",privileges,userName,privilegesInfo,sessionPool);
                }
                // 取消授权
                List<String> cancelPrivileges = privilegesInfo.getCancelPrivileges();
                if (notNullAndNotZero(cancelPrivileges)) {
                    grantOrRevoke("revoke",cancelPrivileges,userName,privilegesInfo,sessionPool);
                }
            }
        }
    }

    @Override
    public RecordVO getRecords(Connection connection, String deviceName, String timeseriesName) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        RecordVO recordVO = new RecordVO();
        List<Date> timeList = new ArrayList<>();
        List<Long> valueList = new ArrayList<>();
        String sql = "select time," + timeseriesName + " from " +  deviceName + " order by time desc limit 200 offset 0";
        try {
            SessionDataSetWrapper sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
            int batchSize = sessionDataSetWrapper.getBatchSize();
            if (batchSize > 0) {
                while (sessionDataSetWrapper.hasNext()) {
                    RowRecord next = sessionDataSetWrapper.next();
                    Date date = new Date(next.getTimestamp());
                    timeList.add(date);
                    List<org.apache.iotdb.tsfile.read.common.Field> fields = next.getFields();
                    Long value = Long.valueOf(fields.get(0).toString());
                    valueList.add(value);
                }
            }
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.GET_RECORD_FAIL,ErrorCode.GET_RECORD_FAIL_MSG);
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.GET_RECORD_FAIL,ErrorCode.GET_RECORD_FAIL_MSG);
        }
        recordVO.setTimeList(timeList);
        recordVO.setValueList(valueList);
        return recordVO;
    }

    @Override
    public SqlResultVO queryAll(Connection connection, List<String> sqls) throws BaseException {
        SessionPool sessionPool = getSessionPool(connection);
        List<String> querySqls = new ArrayList<>();
        String hasResultSql = null;
        for (String sql : sqls) {
            int firstSpaceIndex = sql.indexOf(" ");
            String judge = sql.substring(0, firstSpaceIndex);
            if ("show".equalsIgnoreCase(judge) || "count".equalsIgnoreCase(judge) || "select".equalsIgnoreCase(judge)) {
                hasResultSql = sql;
                continue;
            }
            querySqls.add(sql);
        }
        SqlResultVO sqlResultVO = new SqlResultVO();
        if (hasResultSql != null) {
            sqlResultVO = executeQuery(sessionPool, hasResultSql,false);
        }
        for (String querySql : querySqls) {
            try {
                sessionPool.executeNonQueryStatement(querySql);
            } catch (StatementExecutionException e) {
                logger.error(e.getMessage());
                throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG + ":" + querySql +"执行出错,错误信息["+e.getMessage()+"]");
            } catch (IoTDBConnectionException e) {
                logger.error(e.getMessage());
                throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG + ":" + querySql +"执行出错,错误信息["+e.getMessage()+"]");
            } finally {
                if (sessionPool != null) {
                    sessionPool.close();
                }
            }
        }
        return sqlResultVO;
    }

    private void grantOrRevoke(String word, List<String> privileges,String userName,PrivilegeInfoDTO privilegesInfo,SessionPool sessionPool) throws BaseException {
        Integer type = privilegesInfo.getType();
        String privilegesStr = String.join(",", privileges);
        if (type == 0) {
            String sql = word + " user " + userName + " privileges '" + privilegesStr + "' on root";
            try {
                sessionPool.executeNonQueryStatement(sql);
            } catch (StatementExecutionException e) {
                logger.error(e.getMessage());
                throw new BaseException(ErrorCode.PRIV_ROOT_FAIL,ErrorCode.PRIV_ROOT_FAIL_MSG);
            } catch (IoTDBConnectionException e) {
                logger.error(e.getMessage());
                throw new BaseException(ErrorCode.PRIV_ROOT_FAIL,ErrorCode.PRIV_ROOT_FAIL_MSG);
            }
            return;
        }
        if (type == 1) {
            List<String> groupPaths = privilegesInfo.getGroupPaths();
            if (notNullAndNotZero(groupPaths)) {
                for (String groupPath : groupPaths) {
                    String sql = word + " user " + userName + " privileges '" + privilegesStr + "' on root."
                            + groupPath;
                    try {
                        sessionPool.executeNonQueryStatement(sql);
                    } catch (StatementExecutionException e) {
                        logger.error(e.getMessage());
                        throw new BaseException(ErrorCode.PRIV_GROUP_FAIL,ErrorCode.PRIV_GROUP_FAIL_MSG);
                    } catch (IoTDBConnectionException e) {
                        logger.error(e.getMessage());
                        throw new BaseException(ErrorCode.PRIV_GROUP_FAIL,ErrorCode.PRIV_GROUP_FAIL_MSG);
                    }
                }
            }
            return;
        }
        if (type == 2) {
            List<String> groupPaths = privilegesInfo.getGroupPaths();
            List<String> devicePaths = privilegesInfo.getDevicePaths();
            if (notNullAndNotZero(groupPaths) && groupPaths.size() == 1 && notNullAndNotZero(devicePaths)){
                String onlyGroupPath = groupPaths.get(0);
                for (String devicePath : devicePaths) {
                    String sql = word + " user " + userName + " privileges '" + privilegesStr + "' on root."
                            + onlyGroupPath + "." + devicePath;
                    try {
                        sessionPool.executeNonQueryStatement(sql);
                    } catch (StatementExecutionException e) {
                        logger.error(e.getMessage());
                        throw new BaseException(ErrorCode.PRIV_DEVICE_FAIL,ErrorCode.PRIV_DEVICE_FAIL_MSG);
                    } catch (IoTDBConnectionException e) {
                        logger.error(e.getMessage());
                        throw new BaseException(ErrorCode.PRIV_DEVICE_FAIL,ErrorCode.PRIV_DEVICE_FAIL_MSG);
                    }
                }
            }
            return;
        }
        if (type == 3) {
            List<String> groupPaths = privilegesInfo.getGroupPaths();
            List<String> devicePaths = privilegesInfo.getDevicePaths();
            List<String> timeseriesPaths = privilegesInfo.getTimeseriesPaths();
            if (notNullAndNotZero(groupPaths) && groupPaths.size() == 1 && notNullAndNotZero(devicePaths)
                    && devicePaths.size() == 1 && notNullAndNotZero(timeseriesPaths)){
                String onlyGroupPath = groupPaths.get(0);
                String onlyDevicePath = devicePaths.get(0);
                for (String timeseriesPath : timeseriesPaths) {
                    String sql = word + " user " + userName + " privileges '" + privilegesStr + "' on root."
                            + onlyGroupPath + "." + onlyDevicePath + "." + timeseriesPath;
                    try {
                        sessionPool.executeNonQueryStatement(sql);
                    } catch (StatementExecutionException e) {
                        logger.error(e.getMessage());
                        throw new BaseException(ErrorCode.PRIV_TIMESERIES_FAIL,ErrorCode.PRIV_TIMESERIES_FAIL_MSG);
                    } catch (IoTDBConnectionException e) {
                        logger.error(e.getMessage());
                        throw new BaseException(ErrorCode.PRIV_TIMESERIES_FAIL,ErrorCode.PRIV_TIMESERIES_FAIL_MSG);

                    }
                }
            }
           return;
        }
        throw new BaseException(ErrorCode.NO_TYPE,ErrorCode.NO_TYPE_MSG);
    }


    /**
     * 判断集合不为空且长度大于0
     */
    private boolean notNullAndNotZero(List list) {
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    private String executeQueryOneLine(SessionPool sessionPool, String sql, String queryField) throws BaseException {
        try {
            SessionDataSetWrapper sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
            List<String> columnNames = sessionDataSetWrapper.getColumnNames();
            int index = -1;
            for (int i = 0; i < columnNames.size(); i++) {
                if (queryField.equals(columnNames.get(i))) {
                    index = i;
                }
            }
            if (index == -1) {
                throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG);
            }
            int batchSize = sessionDataSetWrapper.getBatchSize();
            if (batchSize > 0) {
                if (sessionDataSetWrapper.hasNext()) {
                    RowRecord rowRecord = sessionDataSetWrapper.next();
                    return rowRecord.getFields().get(index).toString();
                }
            }
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG);
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG);
        }
        throw new BaseException(ErrorCode.NO_GROUP,ErrorCode.NO_GROUP_MSG);
    }

    private SqlResultVO executeQuery(SessionPool sessionPool, String sql,Boolean closePool) throws BaseException {
        SqlResultVO sqlResultVO = new SqlResultVO();
        List<List<String>> valuelist = new ArrayList<>();
        try {
            SessionDataSetWrapper sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
            long start = System.currentTimeMillis();
            List<String> columnNames = sessionDataSetWrapper.getColumnNames();
            sqlResultVO.setMetaDataList(columnNames);
            int batchSize = sessionDataSetWrapper.getBatchSize();
            // 记录行数
            long count = 0;
            if (batchSize > 0) {
                while (sessionDataSetWrapper.hasNext()) {
                    List<String> strList = new ArrayList<>();
                    RowRecord rowRecord = sessionDataSetWrapper.next();
                    count ++;
                    for (org.apache.iotdb.tsfile.read.common.Field field : rowRecord.getFields()) {
                        strList.add(field.toString());
                    }
                    valuelist.add(strList);
                }
                long end = System.currentTimeMillis();
                double time = (end - start + 0.0d) / 1000;
                String queryTime = time + "s";
                sqlResultVO.setQueryTime(queryTime);
                sqlResultVO.setLine(count);
            }
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG);
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG);
        } finally {
            if (sessionPool != null && closePool) {
                sessionPool.close();
            }
        }
        sqlResultVO.setValueList(valuelist);
        return sqlResultVO;
    }


    private <T> List<T> executeQuery(Class<T> clazz, SessionPool sessionPool, String sql, Integer pageSize, Integer pageNum) throws BaseException {
        SessionDataSetWrapper sessionDataSetWrapper = null;
        try {
            sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
            List<T> results = new ArrayList<>();
            int batchSize = sessionDataSetWrapper.getBatchSize();
            int count = 0;
            if (batchSize > 0) {
                while (sessionDataSetWrapper.hasNext()) {
                    RowRecord rowRecord = sessionDataSetWrapper.next();
                    count++;
                    if (count < pageSize * (pageNum - 1) + 1) {
                        continue;
                    }
                    if (count > pageSize * pageNum) {
                        break;
                    }
                    T t = clazz.newInstance();
                    List<org.apache.iotdb.tsfile.read.common.Field> fields = rowRecord.getFields();
                    List<String> columnNames = sessionDataSetWrapper.getColumnNames();
                    List<TSDataType> columnTypes = sessionDataSetWrapper.getColumnTypes();
                    for (int i = 0; i < fields.size(); i++) {
                        Field field = clazz.getDeclaredField(columnNames.get(i).replaceAll(" ",""));
                        field.setAccessible(true);
                        field.set(t,fields.get(i).toString());
                    }
                    results.add(t);
                }
            }
            return results;
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.GET_MSM_FAIL,ErrorCode.GET_MSM_FAIL_MSG);
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.GET_MSM_FAIL,ErrorCode.GET_MSM_FAIL_MSG);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.GET_MSM_FAIL,ErrorCode.GET_MSM_FAIL_MSG);
        } finally {
            if (sessionDataSetWrapper != null) {
                sessionDataSetWrapper.close();
            }
        }
    }

    private String executeQueryOneValue(SessionPool sessionPool,String sql) throws BaseException {
        SessionDataSetWrapper sessionDataSetWrapper = null;
        try {
            sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
            int batchSize = sessionDataSetWrapper.getBatchSize();
            String value = null;
            if (batchSize > 0) {
                while (sessionDataSetWrapper.hasNext()) {
                    RowRecord rowRecord = sessionDataSetWrapper.next();
                    List<org.apache.iotdb.tsfile.read.common.Field> fields = rowRecord.getFields();
                    value = fields.get(0).toString();
                    break;
                }
            }
            return value;
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.GET_SQL_ONE_VALUE_FAIL,ErrorCode.GET_SQL_ONE_VALUE_FAIL_MSG);
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.GET_SQL_ONE_VALUE_FAIL,ErrorCode.GET_SQL_ONE_VALUE_FAIL_MSG);
        } finally {
            if (sessionDataSetWrapper != null) {
                sessionDataSetWrapper.close();
            }
        }
    }

    private List<String> executeQueryOneColumn(SessionPool sessionPool,String sql,Integer pageSize,Integer pageNum) throws BaseException {
        SessionDataSetWrapper sessionDataSetWrapper = null;
        try {
            sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
            int batchSize = sessionDataSetWrapper.getBatchSize();
            List<String> values = new ArrayList<>();
            int count = 0;
            if (batchSize > 0) {
                while (sessionDataSetWrapper.hasNext()) {
                    RowRecord rowRecord = sessionDataSetWrapper.next();
                    count++;
                    if (count < pageSize * (pageNum - 1) + 1) {
                        continue;
                    }
                    if (count > pageSize * pageNum) {
                        break;
                    }
                    List<org.apache.iotdb.tsfile.read.common.Field> fields = rowRecord.getFields();
                    values.add(fields.get(0).toString());
                }
            }
            return values;
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.GET_SQL_ONE_COLUMN_FAIL,ErrorCode.GET_SQL_ONE_COLUMN_FAIL_MSG);
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.GET_SQL_ONE_COLUMN_FAIL,ErrorCode.GET_SQL_ONE_COLUMN_FAIL_MSG);
        } finally {
            if (sessionDataSetWrapper != null) {
                sessionDataSetWrapper.close();
            }
        }
    }

    private List<String> executeQueryOneColumn(SessionPool sessionPool,String sql) throws BaseException {
        SessionDataSetWrapper sessionDataSetWrapper = null;
        try {
            sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
            int batchSize = sessionDataSetWrapper.getBatchSize();
            List<String> values = new ArrayList<>();
            if (batchSize > 0) {
                while (sessionDataSetWrapper.hasNext()) {
                    RowRecord rowRecord = sessionDataSetWrapper.next();
                    List<org.apache.iotdb.tsfile.read.common.Field> fields = rowRecord.getFields();
                    values.add(fields.get(0).toString());
                }
            }
            return values;
        } catch (IoTDBConnectionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.GET_SQL_ONE_COLUMN_FAIL,ErrorCode.GET_SQL_ONE_COLUMN_FAIL_MSG);
        } catch (StatementExecutionException e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.GET_SQL_ONE_COLUMN_FAIL,ErrorCode.GET_SQL_ONE_COLUMN_FAIL_MSG);
        } finally {
            if (sessionDataSetWrapper != null) {
                sessionDataSetWrapper.close();
            }
        }
    }


    private List<PrivilegeInfo> privilegesStrSwitchToObject(SessionPool sessionPool,List<String> privileges) throws BaseException {
        List<PrivilegeInfo> results = new ArrayList<>();
        List<String> pathStr = new ArrayList<>();
        List<List<String>> privilegeStr = new ArrayList<>();
        HashMap<String,Boolean> rootPrivileges = new HashMap();
        // 遍历集合 将路径和权限集合分别装 过程中 将root相关内容处理
        for (int i = 0; i < privileges.size(); i++) {
            String[] split = privileges.get(i).split(":");
            String[] s = split[1].trim().split(" ");
            // i = 0 时为root特殊处理
            if (i == 0) {
                for (String s1 : s) {
                    if (rootPrivileges.containsKey(s1)) {
                        continue;
                    }
                    rootPrivileges.put(s1,true);
                }
                continue;
            }
            List<String> list = new ArrayList<>();
            pathStr.add(split[0].trim());
            // 其他粒度下 只需要存储SPECIAL_PRIVILEGES四种权限
            for (String s1 : s) {
                if (SPECIAL_PRIVILEGES.containsKey(s1)) {
                    list.add(s1);
                    continue;
                }
                // 除了root这一层级 其他此权限不生效 不添加进root权限集合
                if (NO_NEED_PRIVILEGES.equals(s1)) {
                    continue;
                }
                if (rootPrivileges.containsKey(s1)) {
                    continue;
                }
                rootPrivileges.put(s1,true);
            }
            privilegeStr.add(list);
        }
        // 先处理root 生成对象
        Set<String> strings = rootPrivileges.keySet();
        List<String> rootPrivilege = Arrays.asList(strings.toArray(new String[0]));
        PrivilegeInfo privilegeInfo = new PrivilegeInfo();
        privilegeInfo.setType(0);
        privilegeInfo.setPrivileges(rootPrivilege);
        results.add(privilegeInfo);
        // 处理非root  String存储形式 "权限1 权限2 权限3.." List存储相同并集下的path路径
        Map<String,List<String>> privilegeOne = new HashMap<>();
        Map<String,List<String>> privilegeTwo = new HashMap<>();
        Map<String,List<String>> privilegeThree = new HashMap<>();
        for (int i = 0; i < pathStr.size(); i++) {
            String s = pathStr.get(i);
            List<String> list = privilegeStr.get(i);
            String str = String.join(" ", list);
            // 通过路径获取所属粒度
            int type = findType(sessionPool,s);
            if (type == 1) {
                // 判断相同的权限集合 放入同一list
                if (privilegeOne.containsKey(str)) {
                    List<String> typeList = privilegeOne.get(str);
                    // 相同粒度 同一范围下做前缀判断 相同则为一个并集
                    int existEnd = typeList.get(0).lastIndexOf(".");
                    int end = s.lastIndexOf(".");
                    if (typeList.get(0).substring(0,existEnd).equals(s.substring(0,end))) {
                        typeList.add(s);
                        continue;
                    }
                }
                ArrayList<String> newStr = new ArrayList();
                newStr.add(s);
                privilegeOne.put(str,newStr);
                continue;
            }
            if (type == 2) {
                if (privilegeTwo.containsKey(str)) {
                    List<String> typeList = privilegeTwo.get(str);
                    int existEnd = typeList.get(0).lastIndexOf(".");
                    int end = s.lastIndexOf(".");
                    if (typeList.get(0).substring(0,existEnd).equals(s.substring(0,end))) {
                        typeList.add(s);
                        continue;
                    }
                }
                ArrayList<String> newStr = new ArrayList();
                newStr.add(s);
                privilegeTwo.put(str,newStr);
                continue;
            }
            if (type == 3) {
                if (privilegeThree.containsKey(str)) {
                    List<String> typeList = privilegeTwo.get(str);
                    int existEnd = typeList.get(0).lastIndexOf(".");
                    int end = s.lastIndexOf(".");
                    if (typeList.get(0).substring(0,existEnd).equals(s.substring(0,end))) {
                        typeList.add(s);
                        continue;
                    }
                }
                ArrayList<String> newStr = new ArrayList();
                newStr.add(s);
                privilegeThree.put(str,newStr);
            }
        }
        Set<String> oneKeys = privilegeOne.keySet();
        Set<String> twoKeys = privilegeTwo.keySet();
        Set<String> threeKeys = privilegeThree.keySet();
        // 封装成PrivilegeInfo返回 字符串处理
        for (String oneKey : oneKeys) {
            PrivilegeInfo oneInfo = new PrivilegeInfo();
            List<String> groupPath = new ArrayList<>();
            List<String> list = privilegeOne.get(oneKey);
            for (String s : list) {
                String groupName = s.replaceFirst("root.", "");
                groupPath.add(groupName);
            }
            List<String> privilegesOne = Arrays.asList(oneKey.split(" "));
            String sql = "show storage group";
            List<String> allGroupPathsStr = executeQueryOneColumn(sessionPool, sql);
            List<String> allGroupPaths = new ArrayList<>();
            for (String s : allGroupPathsStr) {
                String field = s.replaceFirst("root.", "");
                allGroupPaths.add(field);
            }
            // 展示数据
            oneInfo.setType(1);
            oneInfo.setPrivileges(privilegesOne);
            // allxxx内容为前端编辑修改时需要的数据
            oneInfo.setGroupPaths(groupPath);
            oneInfo.setAllGroupPaths(allGroupPaths);
            results.add(oneInfo);
        }
        for (String twoKey : twoKeys) {
            PrivilegeInfo twoInfo = new PrivilegeInfo();
            List<String> groupPath = new ArrayList<>();
            List<String> devicePath = new ArrayList<>();
            List<String> list = privilegeTwo.get(twoKey);
            // 得到 组名、设备名、测点名  便于后续字符串操作
            PathVO pathVO = splitPathToPathVO(sessionPool,list.get(0));
            groupPath.add(pathVO.getGroupName());
            for (String s : list) {
                String deviceName = s.replaceFirst("root." + pathVO.getGroupName() + ".", "");
                devicePath.add(deviceName);
            }
            List<String> privilegesTwo = Arrays.asList(twoKey.split(" "));
            String sql = "show storage group";
            List<String> allGroupPathsStr = executeQueryOneColumn(sessionPool, sql);
            List<String> allGroupPaths = new ArrayList<>();
            for (String s : allGroupPathsStr) {
                String field = s.replaceFirst("root.", "");
                allGroupPaths.add(field);
            }
            int end = list.get(0).lastIndexOf(".");
            sql = "show devices " + list.get(0).substring(0,end) + ".*";
            List<String> allDevicePathsStr = executeQueryOneColumn(sessionPool, sql);
            List<String> allDevicePaths = new ArrayList<>();
            for (String s : allDevicePathsStr ) {
                String field = s.replaceFirst(list.get(0).substring(0,end)+".", "");
                allDevicePaths.add(field);
            }
            // 展示数据
            twoInfo.setType(2);
            twoInfo.setPrivileges(privilegesTwo);
            twoInfo.setGroupPaths(groupPath);
            twoInfo.setDevicePaths(devicePath);
            // allxxx内容为前端编辑修改时需要的数据
            twoInfo.setAllGroupPaths(allGroupPaths);
            twoInfo.setAllDevicePaths(allDevicePaths);
            results.add(twoInfo);
        }
        for (String threeKey : threeKeys) {
            PrivilegeInfo threeInfo = new PrivilegeInfo();
            List<String> groupPath = new ArrayList<>();
            List<String> devicePath = new ArrayList<>();
            List<String> timeseriesPath = new ArrayList<>();
            List<String> list = privilegeThree.get(threeKey);
            // 得到 组名、设备名、测点名  便于后续字符串操作
            PathVO pathVO = splitPathToPathVO(sessionPool,list.get(0));
            groupPath.add(pathVO.getGroupName());
            devicePath.add(pathVO.getDeviceName());
            for (String s : list) {
                String timeseriesName = s.replaceFirst("root." + pathVO.getGroupName() + "." + pathVO.getDeviceName() + ".", "");
                timeseriesPath.add(timeseriesName);
            }
            List<String> privilegesOne = Arrays.asList(threeKey.split(" "));
            String sql = "show storage group";
            List<String> allGroupPathsStr = executeQueryOneColumn(sessionPool, sql);
            List<String> allGroupPaths = new ArrayList<>();
            for (String s : allGroupPathsStr) {
                String field = s.replaceFirst("root.", "");
                allGroupPaths.add(field);
            }
            sql = "show devices root." + pathVO.getGroupName() + ".*";
            List<String> allDevicePathsStr = executeQueryOneColumn(sessionPool, sql);
            List<String> allDevicePaths = new ArrayList<>();
            for (String s : allDevicePathsStr) {
                String deviceName = s.replaceFirst("root." + pathVO.getGroupName() + ".", "");
                allDevicePaths.add(deviceName);
            }
            int end = list.get(0).lastIndexOf(".");
            sql = "show timeseries " + list.get(0).substring(0,end) + ".*";
            List<String> allTimeseriesPathsStr = executeQueryOneColumn(sessionPool, sql);
            List<String> allTimeseriesPaths = new ArrayList<>();
            for (String s : allTimeseriesPathsStr) {
                String field = s.replaceFirst(list.get(0).substring(0,end)+".", "");
                allTimeseriesPaths.add(field);
            }
            // 展示数据
            threeInfo.setType(3);
            threeInfo.setPrivileges(privilegesOne);
            threeInfo.setGroupPaths(groupPath);
            threeInfo.setDevicePaths(devicePath);
            threeInfo.setTimeseriesPaths(timeseriesPath);
            // allxxx内容为前端编辑修改时需要的数据
            threeInfo.setAllGroupPaths(allGroupPaths);
            threeInfo.setAllDevicePaths(allDevicePaths);
            threeInfo.setAllTimeseriesPaths(allTimeseriesPaths);
            results.add(threeInfo);
        }
        return results;
    }

    private PathVO splitPathToPathVO(SessionPool sessionPool,String s) throws BaseException {
        PathVO pathVO = new PathVO();
        String sql = "count devices " + s;
        Integer isTimeseries = Integer.valueOf(executeQueryOneValue(sessionPool,sql));
        if (isTimeseries == 0) {
            int mid = s.lastIndexOf(".");
            String timeseriesName = s.substring(mid + 1);
            pathVO.setTimeseriesName(timeseriesName);
            s = s.substring(0,mid);
        }
        String oldS = s;
        int isGroup;
        while (true) {
            sql = "count storage group " + s;
            isGroup = Integer.valueOf(executeQueryOneValue(sessionPool,sql));
            if (isGroup > 0) {
                break;
            }
            int mid = s.lastIndexOf(".");
            s = s.substring(0,mid);
        }
        String deviceName = oldS.replaceFirst(s + ".", "");
        pathVO.setDeviceName(deviceName);
        String groupName = s.replaceFirst("root.", "");
        pathVO.setGroupName(groupName);
        return pathVO;
    }

    private int findType(SessionPool sessionPool, String s) throws BaseException {
        String sql = "count storage group " + s;
        Integer isGroup = Integer.valueOf(executeQueryOneValue(sessionPool, sql));
        if (isGroup == 1) {
            return 1;
        }
        sql = "count devices " + s;
        Integer isDevices = Integer.valueOf(executeQueryOneValue(sessionPool, sql));
        if (isDevices == 1) {
            return 2;
        }
        return 3;
    }

    private List<TSEncoding> handleEncodingStr(List<String> encoding) {
        List<TSEncoding> list = new ArrayList<>();
        for (String s : encoding) {
            switch (s){
                case "PLAIN":
                    list.add(TSEncoding.PLAIN);
                    break;
                case "PLAIN_DICTIONARY":
                    list.add(TSEncoding.PLAIN_DICTIONARY);
                    break;
                case "RLE":
                    list.add(TSEncoding.RLE);
                    break;
                case "DIFF":
                    list.add(TSEncoding.DIFF);
                    break;
                case "TS_2DIFF":
                    list.add(TSEncoding.TS_2DIFF);
                    break;
                case "BITMAP":
                    list.add(TSEncoding.BITMAP);
                    break;
                case "GORILLA_V1":
                    list.add(TSEncoding.GORILLA_V1);
                    break;
                case "REGULAR":
                    list.add(TSEncoding.REGULAR);
                    break;
                case "GORILLA":
                    list.add(TSEncoding.GORILLA);
                    break;
            }
        }
        return list;
    }

    private List<Object> handleValueStr(List<String> values, List<TSDataType> types) throws BaseException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            TSDataType type = types.get(i);
            if (type == TSDataType.BOOLEAN) {
                Integer booleanNum = Integer.valueOf(values.get(i));
                Boolean flag = null;
                if (booleanNum == 0) {
                    flag = false;
                }
                if (booleanNum == 1) {
                    flag = true;
                }
                if (flag != null) {
                    list.add(flag);
                    continue;
                }
                throw new BaseException(ErrorCode.DB_BOOL_WRONG, ErrorCode.DB_BOOL_WRONG_MSG);
            }
            if (type == TSDataType.INT32 || type == TSDataType.INT64) {
                Integer intNum = Integer.valueOf(values.get(i));
                list.add(intNum);
                continue;
            }
            if (type == TSDataType.FLOAT) {
                Float floatNum = Float.valueOf(values.get(i));
                list.add(floatNum);
                continue;
            }
            if (type == TSDataType.DOUBLE) {
                Double doubleNum = Double.valueOf(values.get(i));
                list.add(doubleNum);
                continue;
            }
            list.add(values.get(i));
        }
        return list;
    }

    private List<TSDataType> handleTypeStr(List<String> types) throws BaseException {
        List<TSDataType> list = new ArrayList<>();
        for (String type : types) {
            TSDataType tsDataType;
            switch (type){
                case "BOOLEAN":
                    tsDataType = TSDataType.BOOLEAN;
                    break;
                case "INT32":
                    tsDataType = TSDataType.INT32;
                    break;
                case "INT64":
                    tsDataType = TSDataType.INT64;
                    break;
                case "FLOAT":
                    tsDataType = TSDataType.FLOAT;
                    break;
                case "DOUBLE":
                    tsDataType = TSDataType.DOUBLE;
                    break;
                case "TEXT":
                    tsDataType = TSDataType.TEXT;
                    break;
                default:
                    throw new BaseException(ErrorCode.DB_DATATYPE_WRONG,ErrorCode.DB_DATATYPE_WRONG_MSG);
            }
            list.add(tsDataType);
        }
        return list;
    }


    public static java.sql.Connection getConnection(Connection connection) throws BaseException {
        String driver = "org.apache.iotdb.jdbc.IoTDBDriver";
        String url = "jdbc:iotdb://" + connection.getHost() + ":" + connection.getPort() + "/";
        String username = connection.getUsername();
        String password = connection.getPassword();
        java.sql.Connection conn;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new BaseException(ErrorCode.GET_DBCONN_FAIL,ErrorCode.GET_DBCONN_FAIL_MSG);
        } catch (SQLException e) {
            throw new BaseException(ErrorCode.GET_DBCONN_FAIL,ErrorCode.GET_DBCONN_FAIL_MSG);
        }
        return conn;
    }

    public static SessionPool getSessionPool(Connection connection) throws BaseException {
        String host = connection.getHost();
        Integer port = connection.getPort();
        String username = connection.getUsername();
        String password = connection.getPassword();
        SessionPool sessionPool = null;
        try {
            sessionPool = new SessionPool(host,port,username,password,3);
        } catch (Exception e) {
            throw new BaseException(ErrorCode.GET_SESSION_FAIL,ErrorCode.GET_SESSION_FAIL_MSG);
        }
        return sessionPool;
    }
//    public static SessionPool getSession(Connection connection) throws BaseException {
//        if(sessionPool == null){
//            host = connection.getHost();
//            port = connection.getPort();
//            username = connection.getUsername();
//            password = connection.getPassword();
//            sessionPool = new SessionPool(host,port,username,password,3);
//            return sessionPool;
//        }
//        if(host == connection.getHost() && port.equals(connection.getPort()) && username == connection.getUsername() && password == connection.getPassword()){
//            return sessionPool;
//        }
//        sessionPool.close();
//        host = connection.getHost();
//        port = connection.getPort();
//        username = connection.getUsername();
//        password = connection.getPassword();
//        sessionPool = new SessionPool(host,port,username,password,3);
//        return sessionPool;
//    }

    private void closeConnection(java.sql.Connection conn) throws BaseException {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new BaseException(ErrorCode.CLOSE_DBCONN_FAIL,ErrorCode.CLOSE_DBCONN_FAIL_MSG);
        }
    }

    private String handlerPrivilegeStrToSql(String privilege, String userName, String roleName) {
        int i = privilege.indexOf(":");
        String path = privilege.substring(0, i).trim();
        String[] privileges = privilege.substring(i + 1).trim().split(" ");
        int len = privileges.length;
        if (len == 0) {
            return null;
        }
        StringBuilder str = new StringBuilder();
        if (userName != null) {
            str.append("grant user " + userName + " privileges ");
        } else {
            str.append("grant role " + roleName + " privileges ");
        }
        for (int j = 0; i < len - 1; j++) {
            str.append("'" + privileges[j] + "',");
        }
        str.append("'" + privileges[len - 1] + "' on " + path);
        return str.toString();
    }

    private void customExecute(java.sql.Connection conn, String sql) throws BaseException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG);
                }
            }
            closeConnection(conn);
        }
    }

    private List<String> customExecuteQuery(java.sql.Connection conn, String sql) throws BaseException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.prepareStatement(sql);
            resultSet = statement.executeQuery();
            int columnCount = resultSet.getMetaData().getColumnCount();
            List<String> list = new ArrayList<>();
            while (resultSet.next()) {
                for (int i = 0; i < columnCount; i++) {
                    list.add(resultSet.getString(i + 1));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG);
                }
            }
            closeConnection(conn);
        }
    }

    private <T> List<T> customExecuteQuery(Class<T> clazz, java.sql.Connection conn, String sql) throws BaseException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.prepareStatement(sql);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<T> list = new ArrayList<>();
            while (resultSet.next()) {
                T t = clazz.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    Object value = resultSet.getObject(i + 1);
                    String columnName = metaData.getColumnLabel(i + 1);
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(t, value);
                }
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            throw new BaseException(ErrorCode.QUERY_FAIL,ErrorCode.QUERY_FAIL_MSG);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    throw new BaseException(ErrorCode.QUERY_FAIL,ErrorCode.QUERY_FAIL_MSG);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new BaseException(ErrorCode.QUERY_FAIL,ErrorCode.QUERY_FAIL_MSG);
                }
            }
            closeConnection(conn);
        }
    }




        private SqlResultVO sqlQuery(java.sql.Connection conn, String sql) throws BaseException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.prepareStatement(sql);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            SqlResultVO sqlResultVO = new SqlResultVO();
            List<String> metaDataList = new ArrayList<>();
            for (int i = 0; i < columnCount; i++) {
                metaDataList.add(metaData.getColumnLabel(i + 1));
            }
            sqlResultVO.setMetaDataList(metaDataList);
            List<List<String>> valuelist = new ArrayList<>();
            while (resultSet.next()) {
                List<String> strList = new ArrayList<>();
                for (int i = 0; i < columnCount; i++) {
                    strList.add(resultSet.getString(i + 1));
                }
                valuelist.add(strList);
            }
            sqlResultVO.setValueList(valuelist);
            return sqlResultVO;
        } catch (SQLException e) {
            throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new BaseException(ErrorCode.SQL_EP,ErrorCode.SQL_EP_MSG);
                }
            }
            closeConnection(conn);
        }
    }

    /**
     * 防止sql注入对参数进行校验不能有空格
     * @param field 拼接sql的字段
     */
    private void paramValid(String field) throws BaseException {
        if (field != null) {
            if (!field.matches("^[^ ]+$")) {
                throw new BaseException(ErrorCode.SQL_PARAM_WRONG,ErrorCode.SQL_PARAM_WRONG_MSG);
            }
        }
    }

}