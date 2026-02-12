# LICENSE Updates for All Branches

This directory contains everything needed to update the LICENSE files across all specified branches.

## Quick Summary

需要更新以下5个分支的LICENSE文件，使其与2022.3分支保持一致：
- 2020.1
- 2020.2  
- 2020.3
- IC-2017.2
- intelliJ-14

## Files in This Directory

- `LICENSE-2022.3` - The new license file from 2022.3 branch (Rainbow Brackets Proprietary License)
- `*.patch` - Git patch files for each branch
- `README.md` - This file with instructions

## Option 1: Run the Automated Script (Recommended)

The easiest way is to run the provided script from the repository root:

```bash
./create-license-prs.sh
```

This script will:
1. Fetch each target branch
2. Create a feature branch from it  
3. Apply the LICENSE update
4. Push the feature branch
5. Create a PR automatically using `gh` CLI

**Requirements:**
- Authenticated `gh` CLI or Git credentials configured
- Push access to the repository

## Option 2: Apply Patches Manually

For each branch, you can apply the corresponding patch file:

```bash
# Example for 2020.1
git checkout 2020.1
git apply license-patches/2020.1.patch  
git add LICENSE
git commit -m "Update LICENSE to Rainbow Brackets Proprietary License from 2022.3 branch"
git checkout -b copilot/update-license-2020.1
git push origin copilot/update-license-2020.1

# Then create PR manually via GitHub web UI from copilot/update-license-2020.1 to 2020.1
```

## Option 3: Manual File Replacement

If patches don't apply cleanly:

```bash
# For each branch
git checkout <branch-name>
cp license-patches/LICENSE-2022.3 LICENSE
git add LICENSE
git commit -m "Update LICENSE to Rainbow Brackets Proprietary License from 2022.3 branch"
git checkout -b copilot/update-license-<branch-name>
git push origin copilot/update-license-<branch-name>
# Then create PR via GitHub web UI
```

## Branch Details

### 2020.1
- Current: Apache License 2.0
- Changes: 130 insertions(+), 201 deletions(-)

### 2020.2  
- Current: Apache License 2.0
- Changes: 130 insertions(+), 201 deletions(-)

### 2020.3
- Current: GNU GPL v3
- Changes: 130 insertions(+), 674 deletions(-)

### IC-2017.2
- Current: Apache License 2.0
- Changes: 130 insertions(+), 201 deletions(-)

### intelliJ-14
- Current: No LICENSE file
- Changes: 130 insertions(+) (new file)

## New License Summary

The new license is **Rainbow Brackets Proprietary License**:
- Copyright (c) 2018-present izhangzhihao
- Allows free use of basic features
- Requires paid license for premium features
- Prohibits cracking, modification, and redistribution
- Full text available in `LICENSE-2022.3`
