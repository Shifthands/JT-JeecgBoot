<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol"
          name="PaymentRequestForm">
          <a-row>
            <a-col :span="24">
              <a-form-item label="金额" v-bind="validateInfos.amount" id="PaymentRequestForm-amount" name="amount">
                <a-input-number v-model:value="formData.amount" placeholder="请输入金额" style="width: 100%" />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="备注" v-bind="validateInfos.note" id="PaymentRequestForm-note" name="note">
                <a-textarea v-model:value="formData.note" :rows="4" placeholder="请输入备注" />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="付款事由" v-bind="validateInfos.toptic" id="PaymentRequestForm-toptic" name="toptic">
                <a-textarea v-model:value="formData.toptic" :rows="4" placeholder="请输入付款事由" />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="审批编号" v-bind="validateInfos.oanumber" id="PaymentRequestForm-oanumber"
                name="oanumber">
                <a-input v-model:value="formData.oanumber" placeholder="请输入审批编号" allow-clear></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="发票关联实例" v-bind="validateInfos.ticketinstid" id="PaymentRequestForm-ticketinstid"
                name="ticketinstid">
                <a-input v-model:value="formData.ticketinstid" placeholder="请输入发票关联实例" allow-clear></a-input>
              </a-form-item>
            </a-col>
						<a-col :span="24">
							<a-form-item label=" 发票补充状态" v-bind="validateInfos.ticketstatus" id="PaymentRequestForm-ticketstatus" name="ticketstatus">
								<j-dict-select-tag v-model:value="formData.ticketstatus" dictCode="Req_Ticket_upload_status" placeholder="请选择 发票补充状态"  allow-clear />
							</a-form-item>
						</a-col>
          </a-row>
        </a-form>
      </template>
    </JFormContainer>
  </a-spin>
</template>

<script lang="ts" setup>
import { ref, reactive, defineExpose, nextTick, defineProps, computed, onMounted } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { getValueType } from '/@/utils';
import { saveOrUpdate } from '../PaymentRequest.api';
import { Form } from 'ant-design-vue';
import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';
import JDictSelectTag from '/@/components/Form/src/jeecg/components/JDictSelectTag.vue';
const props = defineProps({
  formDisabled: { type: Boolean, default: false },
  formData: { type: Object, default: () => ({}) },
  formBpm: { type: Boolean, default: true }
});
const formRef = ref();
const useForm = Form.useForm;
const emit = defineEmits(['register', 'ok']);
const formData = reactive<Record<string, any>>({
  id: '',
  amount: undefined,
  note: '',
  toptic: '',
  oanumber: '',
});
const { createMessage } = useMessage();
const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
const confirmLoading = ref<boolean>(false);
//表单验证
const validatorRules = reactive({
});
const { resetFields, validate, validateInfos } = useForm(formData, validatorRules, { immediate: false });

// 表单禁用
const disabled = computed(() => {
  if (props.formBpm === true) {
    if (props.formData.disabled === false) {
      return false;
    } else {
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
      if (record.hasOwnProperty(key)) {
        tmpData[key] = record[key]
      }
    })
    //赋值
    Object.assign(formData, tmpData);
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
</script>

<style lang="less" scoped>
.antd-modal-form {
  padding: 14px;
}
</style>
