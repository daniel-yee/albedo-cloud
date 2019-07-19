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

import com.albedo.java.common.persistence.domain.IdEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author somewhere
 * @since 2019/2/1
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends IdEntity<Role> {

	private static final long serialVersionUID = 1L;

	public static final String F_NAME = "name";

	/**
	 * 锁定标记
	 */
	@NotBlank(message = "锁定标记 不能为空")
	private String lockFlag;

	/**
	 * 数据权限 1全部 2所在机构及以下数据  3 所在机构数据  4仅本人数据 5 按明细设置
	 */
	@NotBlank(message = "数据权限 不能为空")
	private String dataScope;


	@NotBlank(message = "角色名称 不能为空")
	private String name;

	@NotBlank(message = "角色标识 不能为空")
	private String code;

	@NotBlank(message = "角色描述 不能为空")
	private String remark;


}
