import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '金额',
    align: "center",
    dataIndex: 'amount'
  },
  {
    title: '备注',
    align: "center",
    dataIndex: 'note',
    resizable :true
  },
  {
    title: '付款事由',
    align: "center",
    dataIndex: 'toptic',
    resizable :true
  },
  {
    title: '审批编号',
    align: "center",
    dataIndex: 'oanumber',
    resizable :true
  },
  {
    title: '申请人',
    align: "center",
    dataIndex: 'creator'
  },
  {
    title: '所在部门',
    align: "center",
    dataIndex: 'dept'
  },{
    title: ' 发票补充状态',
    align: "center",
    dataIndex: 'ticketstatus',
    resizable :true
  },
  {
    title: '实例id',
    align: "center",
    dataIndex: 'id',
    resizable :true
  },
  {
    title: '发票关联实例',
    align: "center",
    dataIndex: 'ticketinstid',
    resizable :true
  },

];

// 高级查询数据
export const superQuerySchema = {
  amount: {title: '金额',order: 0,view: 'number', type: 'number',},
  note: {title: '备注',order: 1,view: 'textarea', type: 'string',},
  toptic: {title: '付款事由',order: 2,view: 'textarea', type: 'string',},
  oanumber: {title: '审批编号',order: 3,view: 'text', type: 'string',},
  ticketstatus: {title: ' 发票补充状态',order: 4,view: 'list', type: 'string',dictCode: 'Req_Ticket_upload_status',},
  creator: {title: '申请人',order: 5,view: 'text', type: 'string',},
  dept: {title: '所在部门',order: 6,view: 'text', type: 'string',},
};
