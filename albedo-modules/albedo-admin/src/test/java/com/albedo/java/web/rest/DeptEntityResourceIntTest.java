package com.albedo.java.web.rest;

import com.albedo.java.common.core.constant.CommonConstants;
import com.albedo.java.common.core.exception.GlobalExceptionHandler;
import com.albedo.java.modules.admin.AlbedoAdminApplication;
import com.albedo.java.modules.admin.domain.DeptEntity;
import com.albedo.java.modules.admin.resource.DeptResource;
import com.albedo.java.modules.admin.service.DeptService;
import com.albedo.java.modules.admin.vo.DeptDataVo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.albedo.java.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DeptResource REST resource.
 *
 * @see DeptResource
 */
@SpringBootTest(classes = AlbedoAdminApplication.class)
@Slf4j
public class DeptEntityResourceIntTest {


	private String DEFAULT_API_URL;

    private static final String DEFAULT_ANOTHER_NAME = "ANOTHER_NAME";
    private static final String DEFAULT_NAME = "NAME1";
    private static final String UPDATED_NAME = "NAME2";
	private static final String DEFAULT_ANOTHER_PARENTID = "ANOTHER_PARENTID";
//    private static final String DEFAULT_PARENTID = "PARENTID1";
    private static final String UPDATED_PARENTID = "PARENTID2";
	private static final Integer DEFAULT_SORT = 10;
	private static final Integer UPDATED_SORT = 20;
	private static final String DEFAULT_DESCRIPTION = "DESCRIPTION1";
	private static final String UPDATED_DESCRIPTION = "DESCRIPTION2";


    @Autowired
    private DeptService deptService;

    private MockMvc restDeptMockMvc;
	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;
	@Autowired
	private GlobalExceptionHandler globalExceptionHandler;

    private DeptDataVo dept;

	private DeptDataVo anotherDept = new DeptDataVo();
    @BeforeEach
    public void setup() {
		DEFAULT_API_URL = "/dept/";
		MockitoAnnotations.initMocks(this);
		final DeptResource deptResource = new DeptResource(deptService);
		this.restDeptMockMvc = MockMvcBuilders.standaloneSetup(deptResource)
			.setControllerAdvice(globalExceptionHandler)
			.setConversionService(createFormattingConversionService())
			.setMessageConverters(jacksonMessageConverter)
			.build();
    }

    /**
     * Create a DeptEntity.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an domain which has a required relationship to the DeptEntity domain.
     */
    public DeptDataVo createEntity() {
		DeptDataVo dept = new DeptDataVo();
        dept.setName(DEFAULT_NAME);
		dept.setSort(DEFAULT_SORT);
		dept.setDescription(DEFAULT_DESCRIPTION);
        return dept;
    }

    @BeforeEach
    public void initTest() {
        dept = createEntity();
        // Initialize the database

		anotherDept.setName(DEFAULT_ANOTHER_NAME);
		anotherDept.setParentId(DEFAULT_ANOTHER_PARENTID);
		anotherDept.setSort(DEFAULT_SORT);
		anotherDept.setDescription(DEFAULT_DESCRIPTION);
        deptService.save(anotherDept);

        dept.setParentId(anotherDept.getId());
	}

    @Test
    @Transactional
    public void createDept() throws Exception {
        List<DeptEntity> databaseSizeBeforeCreate = deptService.list();

        // Create the DeptEntity
        restDeptMockMvc.perform(post(DEFAULT_API_URL)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dept)))
            .andExpect(status().isOk());

        // Validate the DeptEntity in the database
        List<DeptEntity> deptEntityList = deptService.list();
        assertThat(deptEntityList).hasSize(databaseSizeBeforeCreate.size() + 1);
		DeptEntity testDeptEntity = deptService.findOne(Wrappers.<DeptEntity>query().lambda()
			.eq(DeptEntity::getName, dept.getName()));
		assertThat(testDeptEntity.getName()).isEqualTo(DEFAULT_NAME);
		assertThat(testDeptEntity.getSort()).isEqualTo(DEFAULT_SORT);
		assertThat(testDeptEntity.getParentId()).isEqualTo(anotherDept.getId());
		assertThat(testDeptEntity.getParentIds()).contains(anotherDept.getId());
		assertThat(testDeptEntity.isLeaf()).isEqualTo(true);
		assertThat(testDeptEntity.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
		assertThat(testDeptEntity.getDelFlag()).isEqualTo(DeptEntity.FLAG_NORMAL);
    }

    @Test
    @Transactional
    public void getDept() throws Exception {
        // Initialize the database
        deptService.save(dept);

        // Get the dept
        restDeptMockMvc.perform(get(DEFAULT_API_URL+"{id}", dept.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
			.andExpect(jsonPath("$.data.name").value(DEFAULT_NAME))
			.andExpect(jsonPath("$.data.parentId").value(anotherDept.getId()))
			.andExpect(jsonPath("$.data.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
	public void getNonExistingDept() throws Exception {
        restDeptMockMvc.perform(get("/admin/dept/ddd/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDept() throws Exception {
        // Initialize the database
        deptService.save(dept);
        int databaseSizeBeforeUpdate = deptService.list().size();

        // Update the dept
        DeptEntity updatedDeptEntity = deptService.findOneById(dept.getId());


		DeptDataVo managedDeptVM = new DeptDataVo();
		managedDeptVM.setName(UPDATED_NAME);
		managedDeptVM.setSort(UPDATED_SORT);
		managedDeptVM.setParentId(UPDATED_PARENTID);
		managedDeptVM.setDescription(UPDATED_DESCRIPTION);

        managedDeptVM.setId(updatedDeptEntity.getId());
        restDeptMockMvc.perform(post(DEFAULT_API_URL)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(managedDeptVM)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(CommonConstants.SUCCESS));

        // Validate the DeptEntity in the database
        List<DeptEntity> deptEntityList = deptService.list();
        assertThat(deptEntityList).hasSize(databaseSizeBeforeUpdate);
        DeptEntity testDeptEntity = deptService.findOneById(updatedDeptEntity.getId());
		assertThat(testDeptEntity.getName()).isEqualTo(UPDATED_NAME);
		assertThat(testDeptEntity.getSort()).isEqualTo(UPDATED_SORT);
		assertThat(testDeptEntity.getParentId()).isEqualTo(UPDATED_PARENTID);
//		assertThat(testDeptEntity.getParentIds()).contains(UPDATED_PARENTID);
		assertThat(testDeptEntity.isLeaf()).isEqualTo(true);
		assertThat(testDeptEntity.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
		assertThat(testDeptEntity.getDelFlag()).isEqualTo(DeptEntity.FLAG_NORMAL);
    }



    @Test
    @Transactional
    public void deleteDept() throws Exception {
        // Initialize the database
        deptService.save(dept);
        long databaseSizeBeforeDelete = deptService.findCount();

        // Delete the dept
        restDeptMockMvc.perform(delete(DEFAULT_API_URL+"{id}", dept.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        long databaseSizeAfterDelete = deptService.findCount();
        assertThat(databaseSizeAfterDelete == databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void testDeptEquals() throws Exception {
        TestUtil.equalsVerifier(DeptEntity.class);
        DeptEntity deptEntity1 = new DeptEntity();
        deptEntity1.setId("1");
        deptEntity1.setName("Dept1");
        DeptEntity deptEntity2 = new DeptEntity();
        deptEntity2.setId(deptEntity1.getId());
        deptEntity2.setName(deptEntity1.getName());
        assertThat(deptEntity1).isEqualTo(deptEntity2);
        deptEntity2.setId("2");
        deptEntity2.setName("Dept2");
        assertThat(deptEntity1).isNotEqualTo(deptEntity2);
        deptEntity1.setId(null);
        assertThat(deptEntity1).isNotEqualTo(deptEntity2);
    }

}