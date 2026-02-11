# Rainbow Indent Guide 优化说明（2025 分支）

这份文档用于说明：后续如果继续优化/修改“彩色连线高亮”（`Show rainbow indent guides`），应该重点关注哪些类、各类职责是什么、常见性能瓶颈在哪里。

---

## 1. 你要优先看的核心类

### `src/main/kotlin/com/github/izhangzhihao/rainbow/brackets/lite/indents/RainbowIndentsPassFactory.kt`
- **职责**：注册高亮 pass，决定 pass 在 IDEA 高亮流水线中的执行时机。
- **为什么重要**：延迟体感首先受“pass 排队位置”影响。
- **你会改什么**：
  - `registerTextEditorHighlightingPass(...)` 的 `Anchor` 和 `Pass`。
  - 如果刷新慢，通常先看这里是否排得太晚。

### `src/main/kotlin/com/github/izhangzhihao/rainbow/brackets/lite/indents/RainbowIndentsPass.kt`
- **职责**：计算缩进导线范围、构建/复用 `RangeHighlighter`、绑定 renderer。
- **为什么重要**：这是“数据侧”和“调度侧”核心，重建慢基本都在这里。
- **你会改什么**：
  - `doCollectInformation()`：范围计算、颜色映射策略、是否全量重建。
  - `doApplyInformationToEditor()`：复用高亮器策略、何时重绘。
  - 文档/光标监听：何时触发结构重算、何时只局部刷新。

### `src/main/kotlin/com/github/izhangzhihao/rainbow/brackets/lite/indents/RainbowIndentGuideRenderer.kt`
- **职责**：单条连线怎么画、怎么判断 active（高亮的那条）。
- **为什么重要**：用户感知“像不像 VSCode”主要取决于这里。
- **你会改什么**：
  - `paint(...)` 里的 active/非 active 颜色策略。
  - `isActiveGuide(...)` 里的“只高亮最内层”逻辑。
  - `repaint` 触发后的表现（是否仍有拖影、延迟）。

---

## 2. 关联但次级的类

### `src/main/kotlin/com/github/izhangzhihao/rainbow/brackets/lite/RainbowHighlighter.kt`
- **职责**：提供彩虹级别颜色 key（按 level 映射颜色）。
- **用途**：如果你要改色彩来源、颜色数量/分层策略，会用到它。

### `src/main/kotlin/com/github/izhangzhihao/rainbow/brackets/lite/RainbowInfo.kt`
- **职责**：保存 level、color 和括号范围信息。
- **用途**：如果你要让连线颜色跟括号 visitor 的语义强绑定，可继续扩展这里。

### `src/main/kotlin/com/github/izhangzhihao/rainbow/brackets/lite/settings/RainbowSettings.kt`
- **职责**：功能开关和持久化配置。
- **用途**：新增“性能模式”“高亮策略模式”时要加配置项。

---

## 3. 现有实现中的关键策略（你优化时不要误删）

下面这些是为了降低卡顿而加的关键点：

- **结构变更才重建**（`RainbowIndentsPass`）
  - 同一行普通输入（不改前导缩进、不换行）不应触发全量重建。
  - 只在换行/前导空白变化时递增结构戳并重建。

- **颜色快速映射**
  - 优先按 `indentLevel -> color` 直接取色，避免每条线都走重 PSI 路径。

- **复用 highlighter 时刷新 renderer**
  - 复用 `RangeHighlighter` 时必须更新 `customRenderer`，避免绑定旧 map 后“编辑点后方不高亮”。

- **active 只高亮最内层**
  - 多层包含光标时选最小范围，避免同时亮多条。

- **局部重绘优先**
  - 光标移动尽量只重绘旧 active + 新 active 覆盖区，避免整屏 repaint。

---

## 4. 你未来继续优化时的优先级建议

按收益从高到低：

1. **减少重建次数**  
   先确保“非结构编辑”不重建。

2. **减少单次重建成本**  
   避免 per-range PSI 查询，优先纯文本/level 计算。

3. **减少重绘面积**  
   只刷必要矩形区域，避免 `contentComponent.repaint()` 全屏刷新。

4. **控制监听器频率**  
   连续结构编辑可考虑短防抖（例如 60~120ms）合并重算。

---

## 5. 常见回归（每次改完都要测）

1. **编辑后部分区域不高亮**  
   - 通常是复用高亮器但 renderer/缓存没更新。

2. **必须滚动一下才刷新**  
   - 通常是没有在高亮器替换后触发重绘，或重绘区域太小。

3. **多层都亮，不止最内层**  
   - active 选择逻辑退化，检查最小范围判定。

4. **同一行打字仍明显卡顿**  
   - 说明仍在频繁走全量重建，检查结构戳触发条件。

---

## 6. 建议的最小验证清单

每次优化后，至少跑这组手工验证：

- 在同一行连续输入 20~30 次（不换行）  
  - 连线应几乎无额外重建延迟。

- 在中间行按回车换行、再缩进  
  - 连线应在可接受时间内更新，active 正确。

- 在 200+ 行文件中间编辑  
  - 编辑点上下都能正常高亮，不出现“后半段失效”。

- 多层 `{}` 嵌套移动光标  
  - 只亮最内层一条 active 连线。

---

## 7. 版本策略（你提到的 2025）

如果你只维护 2025 分支：
- 可以大胆使用 2025 平台行为做优化，不必为旧版保守兼容。
- 但仍建议避免依赖“未文档化内部行为”；优先基于公开 API + 本插件可控缓存。

---

如果要继续深挖，我建议下一步做“增量重建（按受影响行窗口）”而不是全量重建，这通常是进一步降低 1~2 秒感知延迟的最大收益项。
