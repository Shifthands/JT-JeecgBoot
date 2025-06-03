<script setup lang="ts">
import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
import { BasicForm, useForm } from '/@/components/Form/index';
import { ref, nextTick } from 'vue';
import PaymentRequestList from '../../PaymentRequest/PaymentRequestListSelect.vue'; // 确保路径正确
// 表单配置
const schemas = [
  {
    field: 'field1',
    component: 'Input',
    label: '字段1',
    colProps: {
      span: 12,
    },
    defaultValue: '111',
  },
  {
    field: 'field2',
    component: 'Input',
    label: '字段2',
    colProps: {
      span: 12,
    },
  },
];

const formValue = ref({
  field1: '',
  field2: ['']
})
// 表单逻辑
const [registerForm, { setFieldsValue }] = useForm({
  labelWidth: 120,
  // schemas,
  showActionButtonGroup: false,
  actionColOptions: {
    span: 24,
  },
});

// 抽屉逻辑
// const [register] = useDrawerInner((data) => {
//   setFieldsValue({
//     field2: data.data,
//     field1: data.info,
//   });
// });
const [register, { closeDrawer }] = useDrawerInner((data) => {
  nextTick(() => { // 添加 nextTick 确保 DOM 更新完成
    setFieldsValue({
      field2: data.data,
      field1: data.info,
    });
    selectedIds.value = data.data
  });

});
const selectedIds = ref([]);
// const selectedIds =field2
// 下一层调用
const handleSelectionChange = (ids) => {
  selectedIds.value = ids;
  // selectRow.value = selectRowFromSelect;
};
function handleOk() {
  // 传回上一层
  emit('selection-confirm', selectedIds);
  closeDrawer();
}
// 添加事件触发
const emit = defineEmits(['selection-change', 'selection-confirm']);

</script>

<template>
  <BasicDrawer v-bind="$attrs" @register="register" title="Drawer Title" width="50%" showFooter @ok="handleOk">
    <BasicForm @register="registerForm" />
    <!-- <div>当前选中: {{ selectedIds }}</div> -->
    <PaymentRequestList @selection-change="handleSelectionChange" :initial-selected-ids="selectedIds" />
  </BasicDrawer>
</template>