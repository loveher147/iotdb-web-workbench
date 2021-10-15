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

import axios from '@/util/axios.js';

export default {
  //获取角色列表
  getRoles(serverId) {
    return axios.get(`/servers/${serverId}/roles`);
  },
  //新增或编辑角色
  editRole(serverId, payload) {
    return axios.post(`/servers/${serverId}/roles`, payload);
  },
  //把指定角色赋予给用户
  grantUserRole(params, payload) {
    let { serverId, roleName } = params;
    return axios.post(`/servers/${serverId}/roles/${roleName}/grant`, payload);
  },
  //修改角色权限管理权限
  editRolePrivilegePower(params, payload) {
    let { serverId, roleName } = params;
    return axios.post(`/servers/${serverId}/roles/${roleName}/authorityPrivilege`, payload);
  },
  //获取角色权限管理权限
  getRolePrivilegePower(params) {
    let { serverId, roleName } = params;
    return axios.get(`/servers/${serverId}/roles/${roleName}/authorityPrivilege`);
  },
  //获取指定角色的信息和用户列表
  getRoleInfo(params) {
    let { serverId, roleName } = params;
    return axios.get(`/servers/${serverId}/roles/${roleName}`);
  },
  //获取用户列表
  getUsers(serverId) {
    return axios.get(`/servers/${serverId}/users`);
  },
  //删除指定角色
  deleteRole(params) {
    let { serverId, roleName } = params;
    return axios.delete(`/servers/${serverId}/roles/${roleName}`);
  },
  //获取角色权限管理权限
  getAuthPrivilege(params) {
    let { serverId, roleName } = params;
    return axios.get(`/servers/${serverId}/roles/${roleName}/authorityPrivilege`);
  },
  //修改角色管理权限
  editAuthPrivilege(params, payload) {
    let { serverId, roleName } = params;
    return axios.post(`/servers/${serverId}/roles/${roleName}/authorityPrivilege`, payload);
  },
  //获取角色数据管理权限
  getDataPrivilege(params) {
    let { serverId, roleName } = params;
    return axios.get(`/servers/${serverId}/roles/${roleName}/dataPrivilege`);
  },
  //修改角色数据管理权限
  editDataPrivilege(params, payload) {
    let { serverId, roleName } = params;
    return axios.post(`/servers/${serverId}/roles/${roleName}/dataPrivilege`, payload);
  },
  //获取存储组列表(节点树形结构)
  getStorageGroupTree(params) {
    let { serverId } = params;
    return axios.get(`/servers/${serverId}/storageGroups/nodeTree`);
  },
  //获取存储组列表(平铺)
  getStorageGroup(params) {
    let { serverId } = params;
    return axios.get(`/servers/${serverId}/storageGroups`);
  },
  //获取指定存储组下的实体列表(节点树形结构)
  getDeviceTreeByGroup(params) {
    let { serverId, groupName } = params;
    return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/nodeTree`);
  },
  //获取指定存储组下的实体列表
  getDeviceByGroup(params) {
    let { serverId, groupName } = params;
    return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices`);
  },
  //获取指定设备下的物理量列表
  getTimeseries(params) {
    let { serverId, groupName, deviceName } = params;
    return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}/timeseries`);
  },
};
