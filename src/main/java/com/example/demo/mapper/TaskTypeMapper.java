package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.TaskTypeDto;
import com.example.demo.entity.TaskType;



@Mapper
public interface TaskTypeMapper {
	
	/**
	 * 作業タイプ情報用DTOリスト取得
     * 
     * @return　作業種別情報用DTOリスト
	 */
	 List<TaskTypeDto> findAll(Integer userId);
		
	/**
	 * 作業タイプ登録、更新
	 * 
	 * @param taskTypes 
	 */
	 void upsert(List<TaskType> taskTypes);

}
