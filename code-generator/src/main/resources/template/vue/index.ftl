<template>
    <div>
        <el-dialog title="提示" v-model="dialogVisible" width="30%">
            <span>确认要删除该条数据吗?</span>
            <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="handleClose">确 定</el-button>
        </span>
            </template>
        </el-dialog>
        <div class="search">
            <el-form label-width="${bean.searchWidth!80}px">
                <el-row>
                    <#if searchs??>
                        <#list searchs as item>
                            <#if item.show>
                                <el-col :span="6" style="padding: 0 8px;">
                                    <el-form-item label="${item.title}">
                                        <#if item.type='date'>
                                            <el-date-picker value-format="yyyy-MM-dd" size="small"
                                                            v-model="searchObject.${item.id!}" type="date"
                                                            placeholder="选择日期">
                                            </el-date-picker>
                                        <#elseif item.type='select'>
                                            <el-select size="small" style="width: 100%;"
                                                       v-model="searchObject.${item.id!}" filterable clearable
                                                       placeholder="请选择">
                                                <el-option v-for="item in ${item.id!}Options" :key="item.id"
                                                           :label="item.name" :value="item.id">
                                                </el-option>
                                            </el-select>
                                        <#elseif item.type='textarea'>
                                            <el-input v-model="searchObject.${item.id!}" size="small" name="${item.id!}"
                                                      type="textarea"></el-input>
                                        <#else>
                                            <el-input v-model="searchObject.${item.id!}" size="small"
                                                      placeholder="${item.placeholder}" name="${item.id!}">
                                            </el-input>
                                        </#if>
                                    </el-form-item>
                                </el-col>
                            </#if>
                        </#list>
                    </#if>

                    <el-col :span="${leftSize!'6'}" style="padding: 0 10px;margin-top: 5px;">
                        <el-row type="flex" justify="end">
                            <el-button size="small" type="primary" @click="search">搜索</el-button>
                            <el-button size="small" plain @click="clearSearch">清除条件</el-button>
                        </el-row>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <div class="data-content">
            <div class="tool-add">
                <el-button type="primary" size="small" @click="addView">新增</el-button>
            </div>

            <el-table v-loading="loading" :data="tableData.list" @sort-change="changeTableSort"
                      style="width: 100%;font-size: 12px;">
                <#if bean.grids??>
                    <#list bean.grids as item>
                        <el-table-column label="${item.title}" prop="${item.id!}${item.extName!}"
                                         <#if item.sort>sortable="custom" </#if>  <#if item.width?length lt 4 > width="${item.width!}"  </#if> >
                        </el-table-column>
                    </#list>
                </#if>
                <el-table-column width="185" align="center" fixed="right" label="操作">
                    <template #default="scope">
                        <el-button text size="small" class="operation_bt" :icon="Edit" type="info"
                                   @click="showView(scope.row)">详情
                        </el-button>

                        <el-button text size="small" class="operation_bt" :icon="Edit" type="primary"
                                   @click="editView(scope.row)">编辑
                        </el-button>

                        <el-button text :icon="deleteIcon" class="operation_bt" type="danger" size="small"
                                   style="cursor: pointer;"
                                   @click="deleteData(scope.row)">删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="page">
                <el-pagination background :current-page="searchObject.no" :page-sizes="[10, 20, 50, 100]"
                               :page-size="tableData.size" :pager-count="5"
                               layout="total, sizes, prev, pager, next, jumper" :page-count="tableData.totalPage"
                               :total="tableData.total" @size-change="sizeChange" @current-change="pageChange">
                </el-pagination>
            </div>
        </div>
    </div>
</template>

<script>
    import {Delete, Edit} from '@element-plus/icons-vue'
    import common from "@/mixins/common.js";

    export default {
        name: "${config_entity}_index",
        mixins: [common],
        data() {
            return {
                Edit: Edit,
                deleteIcon: Delete,
                searchObject: {
                    no: 1,
                    size: 8,
                    <#if searchs??>
                    <#list searchs as item>
                    ${item.id}: ''<#sep>,
                    </#list>
                    </#if>
                },
                adPositionOptions: [],
                dialogVisible: false,
                defaultProps: {
                    children: 'children',
                    label: 'name'
                },
                loading: false,
                tableData: {},
                <#list bean.fields as item>
                <#if item.option?length gt 2 >
                ${item.id}Options: [],
                </#if>
                </#list>
            }
        },
        mounted() {

            let search = this.$tool.data.get("${config_entity}_search");
            if (search) {
                Object.assign(this.searchObject, search)
            }
            this.getSearchList();

            <#list bean.fields as item>
            <#if item.option?length gt 2 >
            this.load${item.id?cap_first}Options();
            </#if>
            </#list>
        },
        methods: {
            async getSearchList() {
                this.loading = true;
                let data = this.searchObject;
                let res = await this.$http.form("/tenantRest/${config_entity}/search.htm", data);
                if (res.code === 0) {
                    this.tableData = res;
                }
                this.loading = false;
                this.$tool.data.set("${config_entity}_search", this.searchObject);

            },
            addView() {
                this.$router.push({
                    path: 'add'
                });
            },
            showView(row) {
                let data = {};
                data.id = row.id;
                this.$router.push({
                    path: 'view',
                    query: data
                });
            },
            editView(row) {
                let data = {};
                data.id = row.id;
                this.$router.push({
                    path: 'update',
                    query: data
                });
            },
            deleteData(row) {
                this.selectId = row.id;
                this.dialogVisible = true;
            },
            <#list bean.fields as item>
            <#if item.option?length gt 2 >
            async load${item.id?cap_first}Options() {
                let self = this;
                var param = {};
                param.sortMethod = "asc";
                param.sortField = "id";
                param.level = 1;
                param.size = 500;
                param.fetch = 0;
                let res = await this.$http.form("/tenantRest/${item.id?lower_case}/list.htm", param);
                if (res.code === 0) {
                    self.${item.id}Options = res.list;
                }
            },
            </#if>
            </#list>
            clearSearch() {
                this.searchObject.key = "";
                this.searchObject.name = "";
                <#if searchs??>
                <#list searchs as item>
                this.searchObject.${item.id} = "";
                </#list>
                </#if>
            },
            async handleClose() {

                this.dialogVisible = false;
                if (this.selectId) {
                    let params = {};
                    params.id = this.selectId;
                    let res = await this.$http.form("/tenantRest/${config_entity}/delete.htm", params);
                    if (res.code === 0) {
                        this.$message({
                            message: '删除数据成功',
                            type: 'success'
                        });
                        await this.getSearchList();
                    } else {
                        this.$message.error(res.msg);
                    }
                }

            }

        },
    }
</script>

<style scoped>

</style>