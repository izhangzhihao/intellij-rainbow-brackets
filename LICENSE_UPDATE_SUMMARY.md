# LICENSE更新任务 - 完成说明

## 任务目标
根据2022.3分支上的最新license把所有分支的license都更新一遍

## 需要更新的分支
1. **2020.1** - Apache License 2.0 → Rainbow Brackets Proprietary License
2. **2020.2** - Apache License 2.0 → Rainbow Brackets Proprietary License
3. **2020.3** - GNU GPL v3 → Rainbow Brackets Proprietary License
4. **IC-2017.2** - Apache License 2.0 → Rainbow Brackets Proprietary License
5. **intelliJ-14** - 无LICENSE文件 → Rainbow Brackets Proprietary License

## 已准备的文件和工具

### 1. Patch文件（推荐使用）
目录: `license-patches/`

每个分支都有对应的patch文件：
- `2020.1.patch`
- `2020.2.patch`
- `2020.3.patch`
- `IC-2017.2.patch`
- `intelliJ-14.patch`

### 2. 新LICENSE文件
文件: `license-patches/LICENSE-2022.3`

包含完整的Rainbow Brackets Proprietary License内容

### 3. 自动化脚本
文件: `./create-license-prs.sh`

一键创建所有PR的自动化脚本。需要：
- 推送权限
- 已配置的git凭据或gh CLI

使用方法：
```bash
./create-license-prs.sh
```

### 4. 详细说明文档
- `MANUAL_PR_GUIDE.md` - 中文手动操作指南（详细步骤）
- `license-patches/README.md` - 英文说明和多种方法

## 快速开始

### 方法一：使用自动化脚本（最简单）
如果你有推送权限和已配置的git凭据：
```bash
cd /path/to/intellij-rainbow-brackets
./create-license-prs.sh
```

脚本会自动：
1. 获取每个目标分支
2. 创建feature分支
3. 应用LICENSE更新
4. 推送到远程
5. 使用gh CLI创建PR

### 方法二：手动应用patch（推荐）
详细步骤见 `MANUAL_PR_GUIDE.md`

简要步骤（以2020.1为例）：
```bash
git fetch origin 2020.1
git checkout 2020.1
git checkout -b copilot/update-license-2020.1
git apply license-patches/2020.1.patch
git add LICENSE
git commit -m "Update LICENSE to Rainbow Brackets Proprietary License from 2022.3 branch"
git push origin copilot/update-license-2020.1
```

然后在GitHub上创建PR: `copilot/update-license-2020.1` → `2020.1`

对其他4个分支重复此过程。

### 方法三：直接复制LICENSE文件
如果patch应用失败：
```bash
git checkout <branch-name>
git checkout -b copilot/update-license-<branch-name>
cp license-patches/LICENSE-2022.3 LICENSE
git add LICENSE
git commit -m "Update LICENSE to Rainbow Brackets Proprietary License from 2022.3 branch"
git push origin copilot/update-license-<branch-name>
```

## PR描述模板
```
根据2022.3分支上的最新license更新本分支的license文件。

## 变更内容
- 将当前许可证更新为Rainbow Brackets Proprietary License
- 确保所有分支的许可证保持一致

## 新许可证说明
- **类型**: Rainbow Brackets Proprietary License
- **版权**: Copyright (c) 2018-present izhangzhihao
- **免费功能**: 基本括号着色功能
- **付费功能**: 高级语言特性需要JetBrains Marketplace购买的许可证
- **限制**: 禁止破解、修改、重新分发

## 原始需求
请你根据2022.3分支上的最新license把所有分支的license都更新一遍
```

## 验证清单
更新完成后，每个分支的LICENSE文件应该：
- [ ] 标题为 "RAINBOW BRACKETS PROPRIETARY LICENSE"
- [ ] 版权行为 "Copyright (c) 2018-present izhangzhihao"
- [ ] 共130行
- [ ] 包含9个主要章节（Permissions, Restrictions, Ownership等）
- [ ] 与 `license-patches/LICENSE-2022.3` 内容完全一致

## 需要创建的PR数量
共需要创建 **5个独立的PR**，每个PR对应一个目标分支。

## 注意事项
1. 每个分支需要单独的PR，不能合并到一起
2. 确保有推送权限才能执行自动化脚本
3. 所有patch文件已经测试并包含完整的变更
4. LICENSE-2022.3文件是从2022.3分支提取的官方版本

## 文件结构
```
.
├── create-license-prs.sh          # 自动化PR创建脚本
├── MANUAL_PR_GUIDE.md             # 中文手动操作指南
├── LICENSE_UPDATE_SUMMARY.md      # 本文件
└── license-patches/               # Patch文件目录
    ├── 2020.1.patch
    ├── 2020.2.patch
    ├── 2020.3.patch
    ├── IC-2017.2.patch
    ├── intelliJ-14.patch
    ├── LICENSE-2022.3             # 新LICENSE内容
    └── README.md                  # 英文说明
```

## 状态
- ✅ 准备工作完成
- ⏳ 等待创建5个PR
- ⏳ 等待PR审核和合并

---
准备完成时间: 2026-02-12
准备者: GitHub Copilot Agent
