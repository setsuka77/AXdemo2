package com.example.demo.batch;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BatchLogMapper {


    /**
     * バッチ実行結果記録
     * 
     * @param batchLog
     */
	void insertBatchLog(BatchLog batchLog);

    /**
     * バッチ履歴ID検索
     * 
     * @param id
     * @return
     */
	BatchLog findBatchLogById(Long id);

    /**
     * バッチ履歴の更新(一応)
     * 
     * @param batchLog
     */
	void updateBatchLog(BatchLog batchLog);

    /**
     * バッチ履歴一覧取得
     * 
     * @return
     */
	List<BatchLog> findAllBatchLogs();
}
