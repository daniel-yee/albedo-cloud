package com.albedo.java.modules.gen.service.impl;

import com.albedo.java.common.core.util.CollUtil;
import com.albedo.java.common.core.util.StringUtil;
import com.albedo.java.common.core.vo.PageModel;
import com.albedo.java.common.core.vo.QueryCondition;
import com.albedo.java.common.data.util.QueryWrapperUtil;
import com.albedo.java.common.persistence.DynamicSpecifications;
import com.albedo.java.common.persistence.SpecificationDetail;
import com.albedo.java.common.persistence.service.impl.DataVoServiceImpl;
import com.albedo.java.modules.sys.domain.Dict;
import com.albedo.java.modules.gen.domain.Scheme;
import com.albedo.java.modules.gen.domain.Table;
import com.albedo.java.modules.gen.domain.TableColumn;
import com.albedo.java.modules.gen.domain.vo.SchemeDataVo;
import com.albedo.java.modules.gen.domain.vo.SchemeVo;
import com.albedo.java.modules.gen.domain.vo.TableDataVo;
import com.albedo.java.modules.gen.domain.vo.TemplateVo;
import com.albedo.java.modules.gen.domain.xml.GenConfig;
import com.albedo.java.modules.gen.repository.SchemeRepository;
import com.albedo.java.modules.gen.repository.TableRepository;
import com.albedo.java.modules.gen.util.GenUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for managing schemes.
 *
 * @author somewhere
 */
@Service
public class SchemeServiceImpl extends DataVoServiceImpl<SchemeRepository, Scheme, String, SchemeDataVo> implements com.albedo.java.modules.gen.service.SchemeService {

    private final TableRepository tableRepository;
    private final TableServiceImpl tableServiceImpl;
    private final TableColumnServiceImpl tableColumnServiceImpl;

    public SchemeServiceImpl(TableRepository tableRepository, TableServiceImpl tableServiceImpl, TableColumnServiceImpl tableColumnServiceImpl) {
        this.tableRepository = tableRepository;
        this.tableServiceImpl = tableServiceImpl;
        this.tableColumnServiceImpl = tableColumnServiceImpl;
    }

    @Override
	public List<Scheme> findAll(String id) {
        SpecificationDetail specificationDetail = DynamicSpecifications.bySearchQueryCondition(
                QueryCondition.ne(Table.F_ID, id == null ? "-1" : id));
        return findAll(specificationDetail);
//		return repository.findAllByStatusAndId(TableDataVo.FLAG_NORMAL, id == null ? "-1" : id);
    }


    @Override
	public String generateCode(SchemeDataVo schemeDataVo) {
        StringBuilder result = new StringBuilder();

        // 查询主表及字段列
		TableDataVo tableDataVo = tableServiceImpl.findOneVo(schemeDataVo.getTableId());
		tableDataVo.setColumnList(tableColumnServiceImpl.findAll(new QueryWrapper<TableColumn>().eq(TableColumn.F_SQL_GENTABLEID,
                tableDataVo.getId()))
                .stream().map(item-> tableColumnServiceImpl.copyBeanToVo( item)).collect(Collectors.toList())
        );
        Collections.sort(tableDataVo.getColumnList());

        // 获取所有代码模板
        GenConfig config = GenUtil.getConfig();

        // 获取模板列表
        List<TemplateVo> templateList = GenUtil.getTemplateList(config, schemeDataVo.getCategory(), false);
        List<TemplateVo> childTableTemplateList = GenUtil.getTemplateList(config, schemeDataVo.getCategory(), true);

        // 如果有子表模板，则需要获取子表列表
        if (childTableTemplateList.size() > 0) {
            tableDataVo.setChildList(tableRepository.findAllByParentTable(tableDataVo.getId())
                    .stream().map(item -> tableServiceImpl.copyBeanToVo(item)).collect(Collectors.toList()));
        }

        // 生成子表模板代码
        if (tableDataVo.getChildList() == null) {
            tableDataVo.setChildList(Lists.newArrayList());
        }
        for (TableDataVo childTable : tableDataVo.getChildList()) {
            Collections.sort(childTable.getColumnList());
            childTable.setCategory(schemeDataVo.getCategory());
            schemeDataVo.setTableDataVo(childTable);
            Map<String, Object> childTableModel = GenUtil.getDataModel(schemeDataVo);
            for (TemplateVo tpl : childTableTemplateList) {
                result.append(GenUtil.generateToFile(tpl, childTableModel, schemeDataVo.getReplaceFile()));
            }
        }
        tableDataVo.setCategory(schemeDataVo.getCategory());
        // 生成主表模板代码
        schemeDataVo.setTableDataVo(tableDataVo);
        Map<String, Object> model = GenUtil.getDataModel(schemeDataVo);
        for (TemplateVo tpl : templateList) {
            result.append(GenUtil.generateToFile(tpl, model, schemeDataVo.getReplaceFile()));
        }
        return result.toString();
    }

    @Override
	public Map<String,Object> findFormData(SchemeDataVo schemeDataVo, String loginId) {
        Map<String, Object> map = Maps.newHashMap();

		if(StringUtil.isNotEmpty(schemeDataVo.getId())){
			schemeDataVo = super.findOneVo(schemeDataVo.getId());
		}
        if (StringUtil.isBlank(schemeDataVo.getPackageName())) {
            schemeDataVo.setPackageName("com.albedo.java.modules");
        }
        if (StringUtil.isBlank(schemeDataVo.getFunctionAuthor())) {
            schemeDataVo.setFunctionAuthor(loginId);
        }
        map.put("schemeVo", schemeDataVo);
        GenConfig config = GenUtil.getConfig();
        map.put("config", config);

        map.put("categoryList", CollUtil.convertComboDataList(config.getCategoryList(), Dict.F_VAL, Dict.F_NAME));
        map.put("viewTypeList", CollUtil.convertComboDataList(config.getViewTypeList(), Dict.F_VAL, Dict.F_NAME));

        List<Table> tableList = tableServiceImpl.findAll(), list = Lists.newArrayList();
		List<String> tableIds =Lists.newArrayList();
        if(StringUtil.isNotEmpty(schemeDataVo.getId())){
			List<Scheme> schemeList = findAll(schemeDataVo.getId());
			tableIds = CollUtil.extractToList(schemeList, "tableId");
		}
        for (Table table : tableList) {
            if (!tableIds.contains(table.getId())) {
                list.add(table);
            }
        }
        map.put("tableList", CollUtil.convertComboDataList(list, Table.F_ID, Table.F_NAMESANDTITLE));
        return map;
    }

	@Override
	public IPage getSchemeVoPage(PageModel pm) {
		Wrapper wrapper = QueryWrapperUtil.getWrapperByPage(pm, getPersistentClass());
		pm.addOrder(OrderItem.desc("a."+Scheme.F_SQL_CREATEDDATE));
		IPage<List<SchemeVo>> userVosPage = baseMapper.getSchemeVoPage(pm, wrapper);
		return userVosPage;
	}


}
