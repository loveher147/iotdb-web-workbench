/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.dto.GroupDTO;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.entity.StorageGroup;

import java.util.List;

public interface GroupService {

  List<String> getGroupDescription(String host, List<String> groupNames) throws BaseException;

  void setStorageGroupInfo(Connection connection, GroupDTO groupDTO) throws BaseException;

  boolean isExist(String host, String groupName);

  void deleteGroupInfo(String host, String groupName) throws BaseException;

  StorageGroup getGroupInfo(String host, String groupName);

  void updateStorageGroupInfo(Connection connection, GroupDTO groupDTO) throws BaseException;

  Integer getGroupId(String host, String groupName);
}
