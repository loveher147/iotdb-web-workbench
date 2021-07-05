import zhLocale from 'element-plus/lib/locale/lang/zh-cn';

const cn = {
  [zhLocale.name]: {
    el: zhLocale.el,
    message: {
      hello: '你好，世界',
    },
    common: {
      submit: '确定',
      cancel: '取消',
      detail: '查看',
      edit: '编辑',
      delete: '删除',
      operation: '操作',
      save: '保存',
    },
    rootPage: {
      chinalang: '中文',
      englishlang: '英文',
      loginoutText: '退出登录',
      about: '关于我们',
      help: '帮助手册',
      databaseManagement: '数据库管理',
      dataList: '数据列表',
      newdatasource: '新建数据源',
      newQueryWindow: '新建查询窗口',
    },
    loginPage: {
      account: '账号',
      password: '密码',
      placeholderAccount: '请输入账号',
      placeholderPassword: '请输入密码',
      forgetPassWord: '忘记密码',
      signIn: '登录',
      forgetPassword: '忘记密码',
      forgetPasswordTip: '请联系系统管理员',
      accountEmptyTip: '账号不能为空',
      passwordEmptyTip: '密码不能为空',
      welcomeLogin: '登录IOTDB数据库管理',
    },

    sourcePage: {
      alias: '数据源名称',
      host: 'IP地址',
      port: '端口',
      username: '用户名',
      password: '密码',
      addDialogTitle: '新增数据源',
      editDialogTitle: '编辑数据源',
      eg: '示例:127.0.0.1',
      aliasEmptyTip: '数据源名称不能为空',
      hostEmptyTip: 'IP地址不能为空',
      portEmptyTip: '端口号不能为空',
      usernameEmptyTip: '用户名不能为空',
      passwordEmptyTip: '密码不能为空',
      userAccount: '用户账号',
      newAccount: '新增账号',
      baseConfig: '基本配置',
      accountPermit: '账号权限',
      userNameTitle: '账号名称：',
      passwordTitle: '账号密码：',
      groupInfo: '存储组信息',
      groupName: '存储组名称',
      description: '描述',
      line: '实体数量',
      path: '权限粒度',
      range: '范围',
      func: '功能',
      selectAlias: '数据连接',
      selectGroup: '存储组',
      selectDevice: '实体',
      selectTime: '测点',
      createGroup: '创建存储组',
      createUser: '创建用户',
      deleteUser: '删除用户',
      editPassword: '修改密码',
      listUser: '查看用户',
      grantPrivilege: '用户赋权',
      revertPrivilege: '撤销权限',
      createTimeSeries: '创建时间序列',
      insertTimeSeries: '插入数据',
      readTimeSeries: '查询数据',
      deleteTimeSeries: '删除数据活时间序列',
      createTrigger: '创建触发器',
      uninstallTrigger: '卸载触发器',
      startTrigger: '启动触发器',
      stopTrigger: '停止触发器',
      createFunction: '注册UDF',
      uninstallFunction: '卸载UDF',
    },
    storagePage: {
      alias: '数据连接',
      creator: '创建人',
      createTime: '创建时间:',
      ttl: '存活时间:',
      description: '存储组描述',
      deviceName: '实体名称:',
      newDevice: '新建实体',
      line: '物理量',
      operation: '操作',
      secondLabel: '秒',
      minuteLabel: '分',
      hourLabel: '小时',
      dayLabel: '天',
      weekLabel: '周',
      monthLabel: '月',
      yearLabel: '年',
      groupName: '存储组名称:',
      groupDescription: '存储组描述:',
      tips: '注:不填代表无限大',
      ttlErrTips: '存活时间和存活时间单位必须同时填写',
    },
    device: {
      devicename: '实体名称',
      description: '实体描述',
      group: '所属存储组',
      physical: '实体物理量',
      addphysical: '添加物理量',
      delete: '删除',
      creator: '创建人',
      createTime: '创建时间',
      newValue: '最新值',
      deletecontent1: '您确定要删除',
      deletecontent2: '删除后，该物理量数据将会丢失，不可恢复',
      cencel: '取消',
      ok: '确定',
      physicalname: '物理量名称',
      datatype: '数据类型',
      codingmode: '编码方式',
      physicaldescr: '物理量描述',
      action: '操作',
      datatrends: '数据趋势',
    },
  },
};

export default cn;
