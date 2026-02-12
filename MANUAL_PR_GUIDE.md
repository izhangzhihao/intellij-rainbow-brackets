# Manual PR Creation Guide

由于自动化脚本无法直接推送到远程仓库，需要手动创建PR。以下是详细步骤：

## 方法一：使用patch文件（推荐）

### 对每个分支执行以下步骤：

#### 1. 2020.1分支

```bash
git fetch origin 2020.1
git checkout 2020.1
git checkout -b copilot/update-license-2020.1
git apply license-patches/2020.1.patch
git add LICENSE
git commit -m "Update LICENSE to Rainbow Brackets Proprietary License from 2022.3 branch"
git push origin copilot/update-license-2020.1
```

然后在GitHub上创建PR：
- Base: `2020.1`
- Compare: `copilot/update-license-2020.1`
- Title: "Update LICENSE to Rainbow Brackets Proprietary License"

#### 2. 2020.2分支

```bash
git fetch origin 2020.2
git checkout 2020.2
git checkout -b copilot/update-license-2020.2
git apply license-patches/2020.2.patch
git add LICENSE
git commit -m "Update LICENSE to Rainbow Brackets Proprietary License from 2022.3 branch"
git push origin copilot/update-license-2020.2
```

创建PR: Base `2020.2` → Compare `copilot/update-license-2020.2`

#### 3. 2020.3分支

```bash
git fetch origin 2020.3
git checkout 2020.3
git checkout -b copilot/update-license-2020.3
git apply license-patches/2020.3.patch
git add LICENSE
git commit -m "Update LICENSE to Rainbow Brackets Proprietary License from 2022.3 branch"
git push origin copilot/update-license-2020.3
```

创建PR: Base `2020.3` → Compare `copilot/update-license-2020.3`

#### 4. IC-2017.2分支

```bash
git fetch origin IC-2017.2
git checkout IC-2017.2
git checkout -b copilot/update-license-IC-2017.2
git apply license-patches/IC-2017.2.patch
git add LICENSE
git commit -m "Update LICENSE to Rainbow Brackets Proprietary License from 2022.3 branch"
git push origin copilot/update-license-IC-2017.2
```

创建PR: Base `IC-2017.2` → Compare `copilot/update-license-IC-2017.2`

#### 5. intelliJ-14分支

```bash
git fetch origin intelliJ-14
git checkout intelliJ-14
git checkout -b copilot/update-license-intelliJ-14
git apply license-patches/intelliJ-14.patch
git add LICENSE
git commit -m "Add LICENSE - Rainbow Brackets Proprietary License from 2022.3 branch"
git push origin copilot/update-license-intelliJ-14
```

创建PR: Base `intelliJ-14` → Compare `copilot/update-license-intelliJ-14`

## 方法二：直接复制LICENSE文件

如果patch文件应用失败，可以直接复制LICENSE文件：

```bash
# 对每个分支
git checkout <branch-name>
git checkout -b copilot/update-license-<branch-name>
cp license-patches/LICENSE-2022.3 LICENSE
git add LICENSE
git commit -m "Update LICENSE to Rainbow Brackets Proprietary License from 2022.3 branch"
git push origin copilot/update-license-<branch-name>
```

## PR描述模板

可以使用以下模板作为PR描述：

```
根据2022.3分支上的最新license更新本分支的license文件。

## 变更内容
- 将当前许可证更新为Rainbow Brackets Proprietary License
- 确保所有分支的许可证保持一致

## 许可证类型
- 新许可证：Rainbow Brackets Proprietary License
- 版权：Copyright (c) 2018-present izhangzhihao
- 允许免费使用基本功能
- 高级功能需要付费许可证
- 禁止破解、修改和重新分发

## 原始需求
请你根据2022.3分支上的最新license把所有分支的license都更新一遍
```

## 一键执行所有命令

如果你有推送权限，可以运行仓库根目录的自动化脚本：

```bash
./create-license-prs.sh
```

此脚本会自动处理所有5个分支并创建PR。

## 注意事项

1. 确保你有推送到远程仓库的权限
2. 确保`gh` CLI已配置（如果使用自动化脚本）
3. 每个分支都需要创建独立的PR
4. patch文件已经包含完整的变更内容
5. LICENSE-2022.3文件包含新的许可证全文

## 验证

应用更改后，验证LICENSE文件包含：
- 标题: "RAINBOW BRACKETS PROPRIETARY LICENSE"
- 版权: "Copyright (c) 2018-present izhangzhihao"
- 共130行
- 包含Permissions、Restrictions、Ownership等章节
