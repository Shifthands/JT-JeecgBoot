import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/PaymentTicket/paymentTicket/list',
  save= '/PaymentTicket/paymentTicket/add',
  edit= '/PaymentTicket/paymentTicket/edit',
  deleteOne = '/PaymentTicket/paymentTicket/delete',
  deleteBatch = '/PaymentTicket/paymentTicket/deleteBatch',
  importExcel = '/PaymentTicket/paymentTicket/importExcel',
  exportXls = '/PaymentTicket/paymentTicket/exportXls',
  paymentTicketDetailList = '/PaymentTicket/paymentTicket/listPaymentTicketDetailByMainId',
  paymentTicketDetailSave= '/PaymentTicket/paymentTicket/addPaymentTicketDetail',
  paymentTicketDetailEdit= '/PaymentTicket/paymentTicket/editPaymentTicketDetail',
  paymentTicketDetailDelete = '/PaymentTicket/paymentTicket/deletePaymentTicketDetail',
  paymentTicketDetailDeleteBatch = '/PaymentTicket/paymentTicket/deleteBatchPaymentTicketDetail',
}
/**
 * 导出api
 * @param params
 */
export const getExportUrl = Api.exportXls;

/**
 * 导入api
 */
export const getImportUrl = Api.importExcel;

/**
 * 列表接口
 * @param params
 */
export const list = (params) =>
  defHttp.get({ url: Api.list, params });

/**
 * 删除单个
 */
export const deleteOne = (params,handleSuccess) => {
  return defHttp.delete({ url: Api.deleteOne, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
}

/**
 * 批量删除
 * @param params
 */
export const batchDelete = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.deleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    }
  });
}

/**
 * 保存或者更新
 * @param params
 */
export const saveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({ url: url, params },{ isTransformResponse: false });
}
  
/**
 * 列表接口
 * @param params
 */
export const paymentTicketDetailList = (params) => {
  if(params['mainid']){
    return defHttp.get({ url: Api.paymentTicketDetailList, params });
  }
  return Promise.resolve({});
}

/**
 * 删除单个
 */
export const paymentTicketDetailDelete = (params,handleSuccess) => {
  return defHttp.delete({ url: Api.paymentTicketDetailDelete, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
}

/**
 * 批量删除
 * @param params
 */
export const paymentTicketDetailDeleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.paymentTicketDetailDeleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    }
  });
}

/**
 * 保存或者更新
 * @param params
 */
export const  paymentTicketDetailSaveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.paymentTicketDetailEdit : Api.paymentTicketDetailSave;
  return defHttp.post({ url: url, params },{ isTransformResponse: false });
}

/**
 * 导入
 */
export const paymentTicketDetailImportUrl = '/PaymentTicket/paymentTicket/importPaymentTicketDetail'

/**
 * 导出
 */
export const paymentTicketDetailExportXlsUrl = '/PaymentTicket/paymentTicket/exportPaymentTicketDetail'
