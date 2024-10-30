package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.TasktypeDto;



@Mapper
public interface TaskTypeMapper {
	
	/**
	 * 作業種別情報用DTOリスト取得
     * 
     * @return　作業種別情報用DTOリスト
	 */
	 List<TasktypeDto> findAll();
		
	

}
