<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol" name="PaymentTicketForm">
          <a-row>
						<a-col :span="24">
							<a-form-item label="发票" v-bind="validateInfos.ticket" id="PaymentTicketForm-ticket" name="ticket">
								<j-upload v-model:value="formData.ticket"   ></j-upload>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item  label="发票校验结果" v-bind="validateInfos.ticketcheck" id="PaymentTicketForm-ticketcheck" name="ticketcheck">
								<a-input :readonly="true" v-model:value="formData.ticketcheck" dictCode="ticket_check_code" placeholder="提交后自动生成发票校验结果"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-row>
                <a-col :span="24">
                  <a-form-item label="关联付款单" v-bind="validateInfos.pamentlist" id="PaymentTicketForm-pamentlist"
                    name="pamentlist">
                    <!-- <a-input :readonly="true" v-model:value="formData.pamentlist" placeholder="选择关联付款单"
                      allow-clear></a-input> -->
                      <a-button preIcon="ant-design:plus-outlined" @click="handleCreate"> 关联付款单</a-button>
                  </a-form-item>
                </a-col>
                <!-- <a-col :span="24" :offset="5">
                  <a-button preIcon="ant-design:plus-outlined" @click="handleCreate"> 关联付款单</a-button>
                </a-col> -->
              </a-row>
						</a-col>
          </a-row>
          <a-row class="up" :gutter="16">
            <a-col class="up" v-for="(item) in cards" :key="item.id" :xs="24" :sm="24" :md="24" :lg="24" >
              <a-card v-if="!closed" class="closable-card">
                <template #title>
                  <span>{{ item.creator }} (审批编号：<Tag color="yellow">{{ item.oanumber }}</Tag>)</span>
                  <span>    金额：<Tag color="green">{{ item.amount }}</Tag></span>
                </template>
                {{ item.content }}
                <template #extra>
                  <a-button type="text" @click="handleClose(item.id)">
                    <close-outlined />
                  </a-button>
                </template>
                <slot></slot>
              </a-card>
            </a-col>
          </a-row>
        </a-form>
        

      </template>
    </JFormContainer>
  </a-spin>
  
  <Drawer4 @register="register4" @selection-confirm="handleSelectionConfirm" width="80%" />
</template>

<script lang="ts" setup>
  import { ref, reactive, defineExpose, nextTick, defineProps, computed, onMounted,watch} from 'vue';
  import { defHttp } from '/@/utils/http/axios';
  import { useMessage } from '/@/hooks/web/useMessage';
  import JUpload from '/@/components/Form/src/jeecg/components/JUpload/JUpload.vue';
  import { getValueType } from '/@/utils';
  import { saveOrUpdate } from '../PaymentTicket.api';
  import { Tag,Form } from 'ant-design-vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';
  import Drawer4 from './Drawer4.vue';
  import {  CloseOutlined } from '@ant-design/icons-vue';
  import JDictSelectTag from '/@/components/Form/src/jeecg/components/JDictSelectTag.vue';

  import { useDrawer } from '/@/components/Drawer';
  

  const props = defineProps({
    formDisabled: { type: Boolean, default: false },
    formData: { type: Object, default: () => ({}) },
    formBpm: { type: Boolean, default: true }
  });
  const useForm = Form.useForm;
  const formData = reactive<Record<string, any>>({
    id: '',
        ticket: '',   
        pamentlist: '',   
  });
  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  //表单验证
  const validatorRules = reactive({
  });
  const { resetFields, validate, validateInfos } = useForm(formData, validatorRules, { immediate: false });
  const formRef = ref();
  // 表单禁用
  const disabled = computed(()=>{
    if(props.formBpm === true){
      if(props.formData.disabled === false){
        return false;
      }else{
        return true;
      }
    }
    return props.formDisabled;
  });

  
  /**
   * 新增
   */
  function add() {
    edit({});
  }

  /**
   * 编辑
   */
  function edit(record) {
    nextTick(() => {
      resetFields();
      const tmpData = {};
      Object.keys(formData).forEach((key) => {
        if(record.hasOwnProperty(key)){
          tmpData[key] = record[key]
        }
      })
      //赋值
      Object.assign(formData,tmpData);
    });
  }

  /**
   * 提交数据
   */
  async function submitForm() {
    try {
      // 触发表单验证
      await validate();
    } catch ({ errorFields }) {
      if (errorFields) {
        const firstField = errorFields[0];
        if (firstField) {
          formRef.value.scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
        }
      }
      return Promise.reject(errorFields);
    }
    confirmLoading.value = true;
    const isUpdate = ref<boolean>(false);
    //时间格式化
    let model = formData;
    if (model.id) {
      isUpdate.value = true;
    }
    //循环数据
    for (let data in model) {
      //如果该数据是数组并且是字符串类型
      if (model[data] instanceof Array) {
        let valueType = getValueType(formRef.value.getProps, data);
        //如果是字符串类型的需要变成以逗号分割的字符串
        if (valueType === 'string') {
          model[data] = model[data].join(',');
        }
      }
    }
    await saveOrUpdate(model, isUpdate.value)
      .then((res) => {
        if (res.success) {
          createMessage.success(res.message);
          emit('ok');
        } else {
          createMessage.warning(res.message);
        }
      })
      .finally(() => {
        confirmLoading.value = false;
      });
  }


  defineExpose({
    add,
    edit,
    submitForm,
  });
  const closed = ref(false);
const emit = defineEmits(['register4', 'ok', 'close1', 'close-card']);
  const cards = ref([])
  enum Api {
  view = '/PaymentRequest/paymentRequest/queryById'
}
const queryById = (id) => {
  let params = { id: id };
  return defHttp.get({ url: Api.view, params }, { isTransformResponse: false });
};

const [register4, { openDrawer: openDrawer4 }] = useDrawer();
// 打开抽屉
function handleCreate() {
  openDrawer4(true, {
    data: formData.pamentlist.split(','),
    info: [],
  });
}
// 下层调用该方法
function handleSelectionConfirm(selectedIds) {
  formData.pamentlist = selectedIds.value.join(',')
}
watch(
  () => formData.pamentlist,
  async (newVal) => {
    const tempList = newVal
      ? newVal.split(',')
      : []
    var reqList: object[]=[]
    for (var index in tempList) {
      var data = await queryById(tempList[index]);
      reqList.push(data.result)
    }
    console.log(reqList)

    cards.value = reqList.map(item => ({
        id: item.id,
        creator: item.creator,
        content: `付款事由: ${item.toptic}`,
        amount:item.amount,
        oanumber:item.oanumber,
      }))

  },
  { immediate: true } // 立即执行一次初始化
)

const handleClose = (cardId) => {
  console.log(cardId)
  const stringArray = ref(formData.pamentlist.split(','));
  stringArray.value = stringArray.value.filter(item => item !== cardId);
  formData.pamentlist = stringArray.value.join(',');
};
</script>

<style lang="less" scoped>
.antd-modal-form {
  padding: 14px;
}

.closable-card {
  position: relative;
}

.closable-card :deep(.ant-card-extra) {
  padding: 0;
}

.up {
  margin: 1%;
}
</style>
