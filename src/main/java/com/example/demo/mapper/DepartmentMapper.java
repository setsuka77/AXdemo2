package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

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
	 * 部署情報 登録
	 *
	 * @param dailyReport 
	 */
	void insert(Department department);
	
	/**
	 * 部署情報 更新
	 *
	 * @param dailyReport 
	 */
	void update(Department department);

}
