import axios from '@/util/axios.js';

//获取实体详情
export function getDeviceDate(serverId, groupName, deviceName) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}`);
}
//获取实体下物理量列表
export function getList(serverId, groupName, deviceName, data) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}/info`, { params: data });
}
//删除物理量
export function deleteData(serverId, groupName, deviceName, timeseriesName) {
  return axios.delete(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}/timeseries/${timeseriesName}`);
}
//新增/编辑实体信息
export function deviceAddEdite(serverId, groupName, data) {
  return axios.post(`/servers/${serverId}/storageGroups/${groupName}/devices`, data);
}
