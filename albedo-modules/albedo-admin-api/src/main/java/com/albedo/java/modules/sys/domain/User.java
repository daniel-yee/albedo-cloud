/*
 *  Copyright (c) 2019-2020, 冷冷 (somewhere0813@gmail.com).
 *  <p>
 *  Licensed under the GNU Lesser General Public License 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 * https://www.gnu.org/licenses/lgpl.html
 *  <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.albedo.java.modules.sys.domain;

import com.albedo.java.common.core.annotation.SearchField;
import com.albedo.java.common.persistence.domain.IdEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author somewhere
 * @since 2019/2/1
 */
@Getter
@Setter
@ToString
@TableName("sys_user")
public class User extends IdEntity<User> {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户名
	 */
	@SearchField
	private String username;

	private String password;
	/**
	 * 随机盐
	 */
	@JsonIgnore
	private String salt;

	/**
	 * 锁定标记
	 */
	private String lockFlag;

	/**
	 * 邮箱
	 */
	@SearchField
	private String email;
	/**
	 * 电话
	 */
	private String phone;
	/**
	 * 头像
	 */
	private String avatar;

	/**
	 * 部门ID
	 */
	private String deptId;

	/**
	 * 微信openId
	 */
	private String wxOpenId;

	/**
	 * QQ openId
	 */
	private String qqOpenId;

}
