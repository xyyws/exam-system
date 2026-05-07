CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT DEFAULT NULL COMMENT '操作人ID',
    username VARCHAR(50) DEFAULT NULL COMMENT '操作人账号',
    real_name VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    operation VARCHAR(200) DEFAULT NULL COMMENT '操作描述',
    method VARCHAR(10) DEFAULT NULL COMMENT '请求方法',
    uri VARCHAR(500) DEFAULT NULL COMMENT '请求URI',
    ip VARCHAR(50) DEFAULT NULL COMMENT '操作IP',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0失败 1成功',
    error_msg VARCHAR(200) DEFAULT NULL COMMENT '错误信息',
    cost_time BIGINT DEFAULT NULL COMMENT '耗时(ms)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_oplog_user_id (user_id),
    KEY idx_oplog_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
