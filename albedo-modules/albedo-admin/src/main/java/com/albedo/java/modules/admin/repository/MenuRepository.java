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

package com.albedo.java.modules.admin.repository;

import com.albedo.java.common.persistence.repository.TreeRepository;
import com.albedo.java.modules.admin.domain.MenuEntity;
import com.albedo.java.modules.admin.vo.MenuVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author somewhere
 * @since 2019/2/1
 */
public interface MenuRepository extends TreeRepository<MenuEntity> {

	/**
	 * 通过角色编号查询菜单
	 *
	 * @param show 是否显示
	 * @return
	 */
	List<MenuVo> listMenuVos(@Param("show") Integer show);

	/**
	 * 通过角色编号查询菜单
	 *
	 * @param roleId 角色ID
	 * @param show 是否显示
	 * @return
	 */
	List<MenuVo> listMenuVosByRoleId(@Param("roleId") String roleId,@Param("show")  Integer show);
	/**
	 * 通过角色ID查询权限
	 *
	 * @param roleIds Ids
	 * @return
	 */
	List<String> listPermissionsByRoleIds(String roleIds);
}
