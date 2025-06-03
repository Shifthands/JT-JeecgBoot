import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '创建人',
    align:"center",
    dataIndex: 'createBy_dictText'
  },
  {
    title: '创建日期',
    align:"center",
    dataIndex: 'createTime'
  },
  {
    title: '所属部门',
    align:"center",
    dataIndex: 'sysOrgCode_dictText'
  },
  {
    title: '发票',
    align:"center",
    dataIndex: 'ticket',
  },
  {
    title: '关联付款单',
    align:"center",
    dataIndex: 'pamentlist'
  },
  {
   title: '发票校验结果',
   align:"center",
   dataIndex: 'ticketcheck_dictText'
  },

];

//子表列表数据
export const paymentTicketDetailColumns: BasicColumn[] = [
  {
    title: '发票号码',
    align:"center",
    dataIndex: 'invoicenumber'
  },
  {
    title: '校验结果',
    align:"center",
    dataIndex: 'checkstatus'
  },
  // {
  //   title: '主表id',
  //   align:"center",
  //   dataIndex: 'mainid'
  // },
];

// 高级查询数据
export const superQuerySchema = {
  createBy: {title: '创建人',order: 0,view: 'sel_user', type: 'string',},
  createTime: {title: '创建日期',order: 1,view: 'datetime', type: 'string',},
  sysOrgCode: {title: '所属部门',order: 2,view: 'sel_depart', type: 'string',},
  ticket: {title: '发票',order: 3,view: 'file', type: 'string',},
  pamentlist: {title: '关联付款单',order: 4,view: 'link_table', type: 'string',},
};
