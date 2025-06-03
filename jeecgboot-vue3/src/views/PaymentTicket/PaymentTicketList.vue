<template>
  <div class="p-2 erpNativeList">
    <!--查询区域-->
    <div class="jeecg-basic-table-form-container">
      <a-form ref="formRef" @keyup.enter.native="reload" :model="queryParam" :label-col="labelCol"
        :wrapper-col="wrapperCol">
        <a-row :gutter="24">
          <a-col :lg="6">
            <a-form-item name="createBy">
              <template #label><span title="创建人">创建人</span></template>
              <j-select-user placeholder="请选择创建人" v-model:value="queryParam.createBy"
                @change="(value) => handleFormJoinChange('createBy', value)" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :lg="6">
            <a-form-item name="createTime">
              <template #label><span title="创建日期">创建日期</span></template>
              <a-range-picker showTime value-format="YYYY-MM-DD HH:mm:ss" v-model:value="queryParam.createTime"
                class="query-group-cust" />
            </a-form-item>
          </a-col>
          <a-col :lg="6">
              <a-form-item name="ticketcheck">
                <template #label><span title="发票校验结果">发票校验</span></template>
                <j-select-multiple placeholder="请选择发票校验结果" v-model:value="queryParam.ticketcheck" dictCode="ticket_check_code" allow-clear />
              </a-form-item>
            </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :lg="6">
            <a-form-item name="pamentlist">
              <template #label><span title="关联付款单">关联付款</span></template>
              <JInput placeholder="请输入关联付款单" v-model:value="queryParam.pamentlist" allow-clear />
            </a-form-item>
          </a-col>
            <a-col :lg="6">
              <a-form-item name="sysOrgCode">
                <template #label><span title="所属部门">所属部门</span></template>
                <j-select-dept placeholder="请选择所属部门" v-model:value="queryParam.sysOrgCode" checkStrictly allow-clear />
              </a-form-item>
            </a-col>
          </template>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-col :lg="6">
                <a-button type="primary" preIcon="ant-design:search-outlined" @click="reload">查询</a-button>
                <a-button preIcon="ant-design:reload-outlined" @click="searchReset"
                  style="margin-left: 8px">重置</a-button>
                <a @click="toggleSearchStatus = !toggleSearchStatus" style="margin-left: 8px">
                  {{ toggleSearchStatus ? '收起' : '展开' }}
                  <Icon :icon="toggleSearchStatus ? 'ant-design:up-outlined' : 'ant-design:down-outlined'" />
                </a>
              </a-col>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="content">
      <!--引用表格-->
      <BasicTable @register="registerTable" :rowSelection="rowSelection">
        <!--插槽:table标题-->
        <template #tableTitle>
          <a-button type="primary" v-auth="'PaymentTicket:payment_ticket:add'" @click="handleAdd"
            preIcon="ant-design:plus-outlined"> 新增</a-button>
          <a-button type="primary" v-auth="'PaymentTicket:payment_ticket:exportXls'"
            preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
          <j-upload-button type="primary" v-auth="'PaymentTicket:payment_ticket:importExcel'"
            preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
          <a-dropdown v-if="selectedRowKeys.length > 0">
            <template #overlay>
              <a-menu>
                <a-menu-item key="1" @click="batchHandleDelete">
                  <Icon icon="ant-design:delete-outlined"></Icon>
                  删除
                </a-menu-item>
              </a-menu>
            </template>
            <a-button v-auth="'PaymentTicket:payment_ticket:deleteBatch'">批量操作
              <Icon icon="mdi:chevron-down"></Icon>
            </a-button>
          </a-dropdown>
          <!-- 高级查询 -->
          <super-query :config="superQueryConfig" @search="handleSuperQuery" />
        </template>
        <!--操作栏-->
        <template #action="{ record }">
          <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
        </template>
        <!--字段回显插槽-->
        <template v-slot:bodyCell="{ column, record, index, text }">
          <template v-if="column.dataIndex === 'ticket'">
            <!--文件字段回显插槽-->
            <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
            <a-button v-else :ghost="true" type="primary" preIcon="ant-design:download-outlined" size="small"
              @click="downloadFile(text)">下载</a-button>
          </template>
        </template>
      </BasicTable>
      <!--子表表格tab-->
      <a-tabs defaultActiveKey="1" style="margin: 10px">
        <a-tab-pane tab="发票子表" key="1">
          <PaymentTicketDetailList />
        </a-tab-pane>
      </a-tabs>
    </div>
    <!-- 表单区域 -->
    <PaymentTicketModal ref="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="PaymentTicket-paymentTicket" setup>
import { ref, reactive, computed, unref, provide,watch, onMounted } from 'vue';
import { BasicTable, useTable, TableAction } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage'
import PaymentTicketModal from './components/PaymentTicketModal.vue'
import { columns, superQuerySchema } from './PaymentTicket.data';
import { list, deleteOne, batchDelete, getImportUrl, getExportUrl } from './PaymentTicket.api';
import { downloadFile } from '/@/utils/common/renderUtils';
import JDictSelectTag from '/@/components/Form/src/jeecg/components/JDictSelectTag.vue';
import JSelectMultiple from '/@/components/Form/src/jeecg/components/JSelectMultiple.vue';
import JSelectDept from '/@/components/Form/src/jeecg/components/JSelectDept.vue';
import JSelectUser from '/@/components/Form/src/jeecg/components/JSelectUser.vue';
import PaymentTicketDetailList from './PaymentTicketDetailList.vue'
import { useUserStore } from '/@/store/modules/user';
import { useRoute } from 'vue-router'
import { cloneDeep } from "lodash-es";
import JInput from "/@/components/Form/src/jeecg/components/JInput.vue";
const formRef = ref();
const route = useRoute()
const queryParam = reactive<{ pamentlist?: string }>({})

  // 初始化读取路由参数
  const initFromRoute = () => {
    if (route.query.pamentlist) {
      queryParam.pamentlist = "*"+route.query.pamentlist.toString()+"*"
    }
  }

  // 组件挂载时执行初始化
  onMounted(initFromRoute)

  // 监听后续路由变化
  watch(() => route.query, (newVal) => {
    if (newVal.pamentlist) {
      queryParam.pamentlist = "*"+newVal.pamentlist.toString()+"*"
    }
  })
const checkedKeys = ref<Array<string | number>>([]);
const registerModal = ref();
const userStore = useUserStore();
//注册table数据
const { prefixCls, tableContext, onExportXls, onImportXls } = useListPage({
  tableProps: {
    title: '付款发票',
    api: list,
    columns,
    canResize: false,
    useSearchForm: false,
    clickToRowSelect: true,
    rowSelection: { type: 'radio' },
    actionColumn: {
      width: 120,
      fixed: 'right'
    },
    beforeFetch: async (params) => {
      let rangerQuery = await setRangeQuery();
      return Object.assign(params, rangerQuery);
    },
    pagination: {
      current: 1,
      pageSize: 5,
      pageSizeOptions: ['5', '10', '20'],
    },
  },
  exportConfig: {
    name: "付款发票",
    url: getExportUrl,
    params: setRangeQuery,
  },
  importConfig: {
    url: getImportUrl,
    success: handleSuccess,
  },
})

const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext
const mainId = computed(() => (unref(selectedRowKeys).length > 0 ? unref(selectedRowKeys)[0] : ''));
//下发 mainId,子组件接收
provide('mainId', mainId);

// 高级查询配置
const superQueryConfig = reactive(superQuerySchema);

/**
 * 高级查询事件
 */
function handleSuperQuery(params) {
  Object.keys(params).map((k) => {
    queryParam[k] = params[k];
  });
  reload();
}

/**
 * 新增事件
 */
function handleAdd() {
  registerModal.value.disableSubmit = false;
  registerModal.value.add();
}

/**
 * 编辑事件
 */
function handleEdit(record: Recordable) {
  registerModal.value.disableSubmit = false;
  registerModal.value.edit(record);
}

/**
 * 详情事件
 */
function handleDetail(record: Recordable) {
  registerModal.value.disableSubmit = true;
  registerModal.value.edit(record);
}

/**
 * 删除事件
 */
async function handleDelete(record) {
  await deleteOne({ id: record.id }, handleSuccess);
}

/**
 * 批量删除事件
 */
async function batchHandleDelete() {
  await batchDelete({ ids: selectedRowKeys.value }, handleSuccess);
}

/**
 * 成功回调
 */
function handleSuccess() {
  (selectedRowKeys.value = []) && reload();
}

/**
 * 操作栏
 */
function getTableAction(record) {
  return [
    {
      label: '编辑',
      onClick: handleEdit.bind(null, record),
      auth: 'PaymentTicket:payment_ticket:edit'
    },
  ];
}

/**
 * 下拉操作栏
 */
function getDropDownAction(record) {
  return [
    {
      label: '详情',
      onClick: handleDetail.bind(null, record),
    },
    {
      label: '删除',
      popConfirm: {
        title: '是否确认删除',
        confirm: handleDelete.bind(null, record),
        placement: 'topLeft'
      },
      auth: 'PaymentTicket:payment_ticket:delete'
    },
  ];
}


/* ----------------------以下为原生查询需要添加的-------------------------- */
const toggleSearchStatus = ref<boolean>(false);
const labelCol = reactive({
  xs: 24,
  sm: 4,
  xl: 6,
  xxl: 4
});
const wrapperCol = reactive({
  xs: 24,
  sm: 20,
});

/**
 * 重置
 */
function searchReset() {
  formRef.value.resetFields();
  selectedRowKeys.value = [];
  //刷新数据
  reload();
}


/**
 * form点击事件(以逗号分割)
 * @param key
 * @param value
 */
function handleFormJoinChange(key, value) {
  if (typeof value != 'string') {
    queryParam[key] = value.join(',');
  }
}

let rangeField = 'createTime,'

/**
 * 设置范围查询条件
 */
async function setRangeQuery() {
  let queryParamClone = cloneDeep(queryParam);
  if (rangeField) {
    let fieldsValue = rangeField.split(',');
    fieldsValue.forEach(item => {
      if (queryParamClone[item]) {
        let range = queryParamClone[item];
        queryParamClone[item + '_begin'] = range[0];
        queryParamClone[item + '_end'] = range[1];
        delete queryParamClone[item];
      } else {
        queryParamClone[item + '_begin'] = '';
        queryParamClone[item + '_end'] = '';
      }
    })
  }
  return queryParamClone;
}
</script>
<style lang="less" scoped>
.jeecg-basic-table-form-container {
  padding: 0;

  .table-page-search-submitButtons {
    display: block;
    margin-bottom: 24px;
    white-space: nowrap;
  }

  .query-group-cust {
    min-width: 100px !important;
  }

  .query-group-split-cust {
    width: 30px;
    display: inline-block;
    text-align: center
  }

  .ant-form-item:not(.ant-form-item-with-help) {
    margin-bottom: 16px;
    height: 32px;
  }

  :deep(.ant-picker),
  :deep(.ant-input-number) {
    width: 100%;
  }
}

.erpNativeList {
  height: 100%;

  .content {
    background-color: #fff;
    height: 100%;
  }
}
</style>