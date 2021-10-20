/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import enLocale from 'element-plus/lib/locale/lang/en';

const en = {
  [enLocale.name]: {
    el: enLocale.el,
    about: {
      'line-2': 'About us',
      'line-3': 'Visual management tool of IotDB',
      'line-4': `IotDB Admin is a GUI interface of IotDB, providing all the Adding, Deleting, Altering and Querying operations. Besides that, Accessing Control is also built. It extremely simplifies the use of IotDB and has very little learning cost. `,
      'line-5': `IotDB is one of the best time series database in our opinion. We will always try our best to advance the development and application of time series database, making contribution to rise of native open source ability and ecosystem development.Welcome everyone of you to join us, waiting for you! Contact us:`,
      'line-6-text': 'Scan wechat',
      'line-7': 'Version: 0.12',
      'back-btn': 'Return to work page',
    },
    common: {
      submit: 'submit',
      cancel: 'cancel',
      detail: 'detail',
      delete: 'delete',
      edit: 'edit',
      operation: 'operation',
      save: 'save',
      clear: 'clear',
    },
    databasem: {
      newStoreGroup: 'New storage group',
      query: 'Query',
      newDevice: 'New entity',
      newQuery: 'New query',
    },
    rootPage: {
      chinalang: 'Chinese',
      englishlang: 'English',
      loginoutText: 'Login out',
      about: 'About us',
      help: 'Help book',
      databaseManagement: 'Database Management',
      dataList: 'Data List',
      newdatasource: 'New data source',
      newQueryWindow: 'New query window',
      nodatasource: 'It is has no data source at present, please',
    },
    loginPage: {
      account: 'Account',
      password: 'Password',
      placeholderAccount: 'Please input account',
      placeholderPassword: 'Please input password',
      forgetPassWord: 'Forget Password',
      signIn: 'Sign in',
      forgetPassword: 'Forget Password',
      forgetPasswordTip: 'Please contact system administrator',
      accountEmptyTip: 'Account can not be empty',
      accountContentTip: 'The user name must be made up of letters, numbers, underscores, and cannot start with numbers and underscores',
      accountLengthTip: 'The user name must be greater than or equal to 3 characters and less than or equal to 32 characters',
      passwordEmptyTip: 'Password can not be empty',
      passwordLenghtTip: 'Password must be greater than or equal to 6 digits. please check the number of digits',
      welcomeLogin: 'Welcome to IotDB Admin',
      loginErrorTip: 'Incorrect user name or password, please re-enter',
    },

    sourcePage: {
      alias: 'Connection',
      host: 'Host',
      port: 'Port',
      storageNum: 'Storage Number',
      entityNum: 'Entity Number',
      physicalNum: 'Physical Number',
      dataNum: 'Data Number',
      username: 'Username',
      password: 'Password',
      addDialogTitle: 'New Source',
      editDialogTitle: 'Edit Source',
      eg: 'Example:127.0.0.1',
      aliasEmptyTip: 'Connection Can Not Be Empty',
      hostEmptyTip: 'Host Can Not Be Empty',
      hostErrorTip: 'Host Pattern Is Incorrect',
      portErrorTip: 'port pattern is incorrect',
      newUserEmptyTip: 'Username Can Not Be Empty',
      newPasswordTip: 'Password Can Not Be Empty',
      newUserErrorTip: 'Please Input Right Pattern',
      newUserErrorTip1: 'The Length Of User Name Must Be Contain With 4~255',
      newpasswordErrorTip1: 'the length of password must be contain with 4~255',
      aliasErrorTip: 'the length of connection must be contain with 3~100',
      portEmptyTip: 'port can not be empty',
      usernameEmptyTip: 'username can not be empty',
      passwordEmptyTip: 'password can not be empty',
      userAccount: 'user account',
      newAccount: 'new account',
      baseConfig: 'base config',
      accountPermit: 'account rights',
      permitPermission: 'Permit Permission',
      permitTips: 'after check the [LIST_USER], you can make the [CREATE_USER]、[DELETE_USER]、[MODIFY_PASSWORD]take effect',
      dataModel: 'Data Model',
      accountRole: 'User Role',
      userNameTitle: 'Username',
      passwordTitle: 'Password',
      roleTitle: 'User Role',
      roleList: 'Role List',
      checkedList: 'Checked  Role List',
      groupInfo: 'Group Info',
      groupName: 'Group Name',
      description: 'description',
      line: 'line',
      path: 'path',
      range: 'range',
      func: 'function',
      selectPermissions: 'Select permissions',
      selectAlias: 'connection',
      selectGroup: 'group',
      selectDevice: 'entity',
      selectTime: 'time series',
      createGroup: 'SET_STORAGE_GROUP',
      createUser: 'CREATE_USER',
      deleteUser: 'DELETE_USER',
      editPassword: 'MODIFY_PASSWORD',
      listRole: 'LIST_ROLE',
      deleteRole: 'DELETE_ROLE',
      grantRolePrivilege: 'GRANT_ROLE_PRIVILEGE',
      revertRolePrivilege: 'REVOKE_ROLE_PRIVILEGE',
      listUser: 'LIST_USER',
      grantPrivilege: 'GRANT_USER_PRIVILEGE',
      revertPrivilege: 'REVOKE_USER_PRIVILEGE',
      revokeUserRole: 'REVOKE_USER_ROLE',
      grantUserRole: 'GRANT_USER_ROLE',
      createRole: 'CREATE_ROLE',
      createTimeSeries: 'CREATE_TIMESERIES',
      insertTimeSeries: 'INSERT_TIMESERIES',
      readTimeSeries: 'READ_TIMESERIES',
      deleteTimeSeries: 'DELETE_TIMESERIES',
      createTrigger: 'CREATE_TRIGGER',
      uninstallTrigger: 'DROP_TRIGGER',
      startTrigger: 'START_TRIGGER',
      stopTrigger: 'STOP_TRIGGER',
      createFunction: 'CREATE_FUNCTION',
      uninstallFunction: 'DROP_FUNCTION',
      test: 'connection test',
      testBtnLabel: 'test',
      testResult: 'connection test passed',
      noAuthTip: 'you have no right to take this operation at present',
      addAuthBtn: 'add auth',
      modifySuccessLabel: 'you have modify password successful',
      addSuccessLabel: 'you have create user successful',
      editSuccessLabel: 'you have edit user successful',
      deleteUserSuccessLabel: 'you have delete user successful',
      addFirstLabel: 'please do create user present first',
      deleteAuthLabel: 'you have delete authorition successful',
      operateAuthLabel: 'you have operate authorition successful',
      deleteGroupLabel: 'you have delete group successful',
      newGroupSuccessLabel: 'you have new or edit group successful',
      deleteAuthConfirm: 'are you sure to delete this auth?',
      deleteSourceConfirm: 'are you sure to delete this source?',
      deleteUserConfirm: 'are you sure to delete this user?',
      deleteRoleConfirm: 'are you sure to delete this role?',
      newSourceSuccessLabel: 'you have new or edit source successful',
      addAuthFirstLabel: 'please do auth add present first',
      authTips: 'attention please: after check the [LIST_USER], you can make the [CREATE_USER]、[DELETE_USER]、[MODIFY_PASSWORD]、[GRANT_USER_PRIVILEGE]、[REVOKE_USER_PRIVILEGE] take effect',
      submitRangeTips: 'range must not be empty',
      submitTypeTips: 'type must not be empty',
      submitPrivilegesTips: 'function must not be empty',
      groupNameLabel: 'group:',
      deviceNameLabel: 'entity:',
      timeNameLabel: 'timeseries:',
      ttlErrorTips: 'ttl must be an positive integer',
      addRole: 'add role',
      dataManagePrivilege: 'data manage privilege',
      roleName: 'role name',
      roleDescription: 'role description',
      grantUserPrivilege: 'grant user privilege',
      addUser: 'add user',
      inputRoleNameTip: 'Please enter a role name of more than 4 characters',
      inputRoleDescTip: 'Please enter role description',
      roleNameLengthError: 'Insufficient role length',
      roleNameExistError: 'Role already exists',
      grantUser: 'Authorized user',
      userList: 'User list',
      selectedUser: 'Selected users',
      showMore: 'Show More',
      nextPage: 'Next Page',
      prePage: 'Pre Page',
      userRelevance: 'User Relevance',
      roleRelevance: 'Role Relevance',
      udf: 'UDF',
      trigger: 'trigger',
    },
    storagePage: {
      alias: 'connection',
      creator: 'creator',
      createTime: 'createTime:',
      ttl: 'ttl:',
      description: 'description',
      deviceName: 'entity name:',
      newDevice: 'new entity',
      line: 'line',
      operation: 'operation',
      secondLabel: 'second',
      minuteLabel: 'minute',
      hourLabel: 'hour',
      dayLabel: 'day',
      weekLabel: 'week',
      monthLabel: 'month',
      yearLabel: 'year',
      groupName: 'group name:',
      groupDescription: 'group description:',
      tips: 'tips:it means that ttl will infinite if you do not fill',
      ttlErrTips: 'ttl and ttl unit must be exist at the same time',
      groupNamePlaceholder: 'please input group name,eg:factory.robotA',
      groupNameLengthTips: 'the length of group name must be contain with 0~255',
      descriptionLengthTips: 'the length of group name must be contain with 0~100',
      deleteGroupConfirm: 'are you sure to delete this group?',
      deleteDeviceConfirm: 'This operation will lead to the deletion of the device could not be resumed, confirmed to delete?',
    },
    device: {
      serchPy: 'Please input the measuring metrics',
      dataconnection: 'data connection',
      selectdataconnection: 'Please select save group',
      selectdataconnections: 'Please select data connections',
      devicename: 'entity name',
      description: 'entity description',
      group: 'storage group',
      physical: 'physical metrics',
      addphysical: 'add physical metrics',
      delete: 'delete',
      creator: 'creator',
      createTime: 'createTime',
      newValue: 'new value',
      deletecontent1: 'Are you sure you want to delete',
      deletecontent2: 'After deletion, the  data will be lost and cannot be recovered',
      cencel: 'cencel',
      ok: 'ok',
      physicalname: 'metrics',
      datatype: 'data type',
      codingmode: 'coding mode',
      physicaldescr: 'description of metrics',
      action: 'operation',
      datatrends: 'data trends',
      datatrend: 'Trend of physical quantity data',
      time: 'date',
      look: 'look',
      inputdevice: 'Please enter entity name',
      inputdecr: 'Please enter entity description',
      pyname: 'Physical quantities must be composed of words, numbers and underscores',
      pynamel: 'The physical quantity must be composed of words, numbers and underscores. The physical quantity must be 0 characters and 255 characters',
      pynamecopy: 'Duplicate physical quantity name, please modify',
      deletetitle: 'Successfully deleted',
      canceldeletion: 'Cancel deletion',
      addpydataa: 'Add up to 2000 pieces of data',
      tips: 'Tips',
      pleaseinput: 'Please fill in the name of physical metrics',
      selectdata: 'Data type must be selected for physical metrics',
      savesuccess: 'Saved successfully',
      minphysical: 'The physical quantity cannot be 0',
      must: 'Please enter the required content',
      newquery: 'New Query',
      inputfunction: 'Please enter a function name',
      selectp: 'Please select entity',
      deleteSuccess: 'Delete Success',
      selectdatatype: 'Please select data type',
    },
    sqlserch: {
      Aggregate: 'Aggregate',
      math: 'Math',
      string: 'String',
      select: 'Select',
      sum: 'Trend calculation',
      date: 'Date',
      count: 'Count the number of time series',
      avg: 'Average value of time series data',
      sum1: 'Sum of time series data',
      fristvalue: 'First inserted data value',
      lastvalue: 'Latest inserted data value',
      minvalue: 'Minimum data value',
      maxvalue: 'Maximum data value',
      mintime: 'Minimum time stamp',
      maxtime: 'Maximum time stamp',
      sin: 'Sine function',
      cos: 'cosine function',
      tan: 'Tangent function',
      asin: 'Anti sine function',
      acos: 'Arccosine function',
      atan: 'Arctangent function',
      degress: 'Angle degree',
      randians: 'Rotation radian',
      sing: 'Symbolic function',
      ceil: 'Round up',
      floor: 'Round down',
      round: 'rounding',
      exp: 'E-based index',
      ln: 'Logarithm with base e',
      log10: 'Base 10 logarithm',
      sqrt: 'take a square root',
      abs: 'Find the absolute value',
      string_cont: 'Used to determine whether a string exists in a string',
      string_mat: 'Used to determine whether a string can be matched by a regular expression regex',
      tok: 'Returns the k data points with the largest median value in a time series',
      bottomk: 'Return the k data points with the minimum value in a time series',
      time_d: 'The difference between the time stamp of a data point and the time stamp of the previous data point in the statistical sequence',
      diff: 'Statistics the difference between the value of a data point and the value of the previous data point in the sequence',
      non: 'The absolute value of the difference between the value of a data point and the value of the previous data point in the statistical sequence',
      deriv: 'The rate of change of a data point in a statistical sequence relative to the previous data point',
      non_n: 'The absolute value of the change rate of a data point relative to the previous data point in a statistical sequence',
      now: 'now date',
      save: 'save',
      run: 'run',
      stop: 'stop',
      sqlserchText: 'The query is executed successfully, and no data is returned',
      sqlrun: 'The query is running. Please do not repeat the operation',
    },
    standTable: {
      export: 'export',
      running: 'Running results',
      download: 'download',
      maxdownload: 'Download up to 100000 pieces of data',
      serchtime: 'Query time',
      queryline: 'Number of query lines',
      function: 'function',
      data: 'data',
      savequery: 'save query',
      queryname: 'query name',
    },
  },
};

export default en;
