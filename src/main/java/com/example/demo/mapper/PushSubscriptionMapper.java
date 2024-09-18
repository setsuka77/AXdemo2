package com.example.demo.mapper;

import com.example.demo.entity.PushSubscription;

import java.util.List;

import org.apache.ibatis.annotations.*;

@Mapper
public interface PushSubscriptionMapper {

    // サブスクリプションを取得するクエリ
	List<PushSubscription> findByUserId(@Param("list") List<Integer> userId);

    // サブスクリプションを保存するクエリ
    void insertSubscription(PushSubscription subscription);

    // サブスクリプションを削除するクエリ
    void deleteByUserId(@Param("userId") Integer userId);
    
    List<PushSubscription> findAll();
}
