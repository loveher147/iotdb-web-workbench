import 'element-plus/packages/theme-chalk/src/base.scss';
import {
  ElLoading,
  ElPagination,
  // ElDialog,
  // ElAutocomplete,
  // ElDropdown,
  // ElDropdownMenu,
  // ElDropdownItem,
  // ElEmpty,
  // ElMenu,
  // ElSubmenu,
  // ElMenuItem,
  // ElMenuItemGroup,
  ElInput,
  ElInputNumber,
  // ElRadio,
  // ElRadioGroup,
  // ElRadioButton,
  // ElCheckbox,
  // ElCheckboxButton,
  // ElCheckboxGroup,
  // ElSwitch,
  ElSelect,
  ElOption,
  // ElOptionGroup,
  ElButton,
  ElButtonGroup,
  ElTable,
  ElTableColumn,
  ElDatePicker,
  // ElTimeSelect,
  ElTimePicker,
  ElPopover,
  ElTooltip,
  ElBreadcrumb,
  ElBreadcrumbItem,
  ElForm,
  ElFormItem,
  ElTabs,
  ElTabPane,
  ElTag,
  ElTree,
  ElAlert,
  // ElSlider,
  // ElIcon,
  // ElRow,
  // ElCol,
  // ElUpload,
  // ElProgress,
  // ElSpinner,
  // ElBadge,
  // ElCard,
  // ElRate,
  // ElSteps,
  // ElStep,
  // ElCarousel,
  // ElCarouselItem,
  ElCollapse,
  ElCollapseItem,
  ElCascader,
  // ElColorPicker,
  // ElTransfer,
  // ElContainer,
  // ElHeader,
  // ElAside,
  // ElMain,
  // ElFooter,
  // ElTimeline,
  // ElTimelineItem,
  // ElLink,
  // ElDivider,
  // ElImage,
  // ElCalendar,
  // ElBacktop,
  // ElPageHeader,
  // ElCascaderPanel,
  // ElDrawer,
  // ElMessageBox,
  // ElMessage,
  ElPopconfirm,
  // ElNotification
} from 'element-plus';

export default {
  install: (Vue) => {
    Vue.use(ElPopconfirm);
    Vue.use(ElPagination);
    // Vue.use(ElDrawer);
    // Vue.use(ElDialog);
    // Vue.use(ElAutocomplete);
    // Vue.use(ElDropdown);
    // Vue.use(ElDropdownMenu);
    // Vue.use(ElDropdownItem);
    // Vue.use(ElEmpty);
    // Vue.use(ElMenu);
    // Vue.use(ElSubmenu);
    // Vue.use(ElMenuItem);
    // Vue.use(ElMenuItemGroup);
    Vue.use(ElInput);
    Vue.use(ElInputNumber);
    // Vue.use(ElRadio);
    // Vue.use(ElRadioGroup);
    // Vue.use(ElRadioButton);
    // Vue.use(ElCheckbox);
    // Vue.use(ElCheckboxButton);
    // Vue.use(ElCheckboxGroup);
    // Vue.use(ElSwitch);
    Vue.use(ElSelect);
    Vue.use(ElOption);
    // Vue.use(ElOptionGroup);
    Vue.use(ElButton);
    Vue.use(ElButtonGroup);
    Vue.use(ElTable);
    Vue.use(ElTableColumn);
    Vue.use(ElDatePicker);
    // Vue.use(ElTimeSelect);
    Vue.use(ElTimePicker);
    Vue.use(ElPopover);
    Vue.use(ElTooltip);
    Vue.use(ElBreadcrumb);
    Vue.use(ElBreadcrumbItem);
    Vue.use(ElForm);
    Vue.use(ElFormItem);
    Vue.use(ElTabs);
    Vue.use(ElTabPane);
    Vue.use(ElTag);
    Vue.use(ElTree);
    Vue.use(ElAlert);
    // Vue.use(ElSlider);
    // Vue.use(ElIcon);
    // Vue.use(ElRow);
    // Vue.use(ElCol);
    // Vue.use(ElUpload);
    // Vue.use(ElProgress);
    // Vue.use(ElSpinner);
    // Vue.use(ElBadge);
    // Vue.use(ElCard);
    // Vue.use(ElRate);
    // Vue.use(ElSteps);
    // Vue.use(ElStep);
    // Vue.use(ElCarousel);
    // Vue.use(ElCarouselItem);
    Vue.use(ElCollapse);
    Vue.use(ElCollapseItem);
    Vue.use(ElCascader);
    // Vue.use(ElColorPicker);
    // Vue.use(ElTransfer);
    // Vue.use(ElContainer);
    // Vue.use(ElHeader);
    // Vue.use(ElAside);
    // Vue.use(ElMain);
    // Vue.use(ElFooter);
    // Vue.use(ElTimeline);
    // Vue.use(ElTimelineItem);
    // Vue.use(ElLink);
    // Vue.use(ElDivider);
    // Vue.use(ElImage);
    // Vue.use(ElCalendar);
    // Vue.use(ElBacktop);
    // Vue.use(ElPageHeader);
    // Vue.use(ElCascaderPanel);
    // Vue.prototype.$message = ElMessage;
    // Vue.prototype.$alert = ElMessageBox.alert;
    // Vue.prototype.$confirm = ElMessageBox.confirm;
    Vue.use(ElLoading.directive);
  },
};
