-- 注意：该页面对应的前台目录为views/PaymentRequest文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2025051903368820310', NULL, '付款申请', '/PaymentRequest/paymentRequestList', 'PaymentRequest/PaymentRequestList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2025-05-19 15:36:31', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051903368830311', '2025051903368820310', '添加付款申请', NULL, NULL, 0, NULL, NULL, 2, 'PaymentRequest:payment_request:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-19 15:36:31', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051903368830312', '2025051903368820310', '编辑付款申请', NULL, NULL, 0, NULL, NULL, 2, 'PaymentRequest:payment_request:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-19 15:36:31', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051903368830313', '2025051903368820310', '删除付款申请', NULL, NULL, 0, NULL, NULL, 2, 'PaymentRequest:payment_request:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-19 15:36:31', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051903368830314', '2025051903368820310', '批量删除付款申请', NULL, NULL, 0, NULL, NULL, 2, 'PaymentRequest:payment_request:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-19 15:36:31', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051903368830315', '2025051903368820310', '导出excel_付款申请', NULL, NULL, 0, NULL, NULL, 2, 'PaymentRequest:payment_request:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-19 15:36:31', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051903368830316', '2025051903368820310', '导入excel_付款申请', NULL, NULL, 0, NULL, NULL, 2, 'PaymentRequest:payment_request:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-19 15:36:31', NULL, NULL, 0, 0, '1', 0);