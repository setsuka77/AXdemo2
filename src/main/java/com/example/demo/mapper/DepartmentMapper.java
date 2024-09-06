package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.DepartmentDto;
import com.example.demo.entity.Department;

/*
 * 部署テーブルマッパー
 * テーブル名：Department
 */
@Mapper
public interface DepartmentMapper {
	
	/**
     * 稼働中部署情報用DTOリスト取得
     * 
     * @return 稼働中部署情報用DTOリスト
     */
	List<DepartmentDto> findAllWork();
	
	
	/**
     * 停止中部署情報用DTOリスト取得
     * 
     * @return 停止中部署情報用DTOリスト
     */
	List<DepartmentDto> findAllStop();
	
	/**
	 * 稼働中部署名 部分一致検索用DTOリスト取得
	 * 
	 * @param name
	 * @return
	 */
    List<DepartmentDto> findByNameLike(@Param("name") String name);
    
    /**
     * 部署名でID検索
     *
     * @return departmentId
     */
    Department findByName(@Param("currentDepartment") String currentDepartment);
	
	/**
	 * 新規登録ボタン押下時 登録処理
	 *　
	 * @param department
	 */
	void insert(Department department);
	
	/**
	 * 部署名変更ボタン押下時 更新処理
	 *
	 * @param department 
	 */
	void update(Department department);
	
	/**
	 * 部署停止ボタン押下時 論理削除処理
	 *
	 * @param department
	 */
	void deactivate(Department department);

}
