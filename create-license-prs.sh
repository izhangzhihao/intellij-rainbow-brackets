#!/bin/bash
# Script to create PRs for LICENSE updates on all branches
# This script should be run by someone with push access to the repository

set -e

REPO_DIR="/home/runner/work/intellij-rainbow-brackets/intellij-rainbow-brackets"
cd "$REPO_DIR"

# Array of target branches
BRANCHES=("2020.1" "2020.2" "2020.3" "IC-2017.2" "intelliJ-14")

# Get the LICENSE content from 2022.3 branch
echo "Fetching LICENSE from 2022.3 branch..."
git fetch origin 2022.3
git show origin/2022.3:LICENSE > /tmp/new_license.txt

# Process each branch
for BRANCH in "${BRANCHES[@]}"; do
    echo "========================================="
    echo "Processing branch: $BRANCH"
    echo "========================================="
    
    # Fetch the target branch
    git fetch origin "$BRANCH:$BRANCH" 2>/dev/null || git fetch origin "$BRANCH"
    
    # Create a feature branch name
    FEATURE_BRANCH="copilot/update-license-${BRANCH}"
    
    # Checkout the target branch
    git checkout "$BRANCH"
    
    # Create and switch to feature branch
    git checkout -b "$FEATURE_BRANCH" || git checkout "$FEATURE_BRANCH"
    
    # Copy the new LICENSE
    cp /tmp/new_license.txt LICENSE
    
    # Add and commit
    git add LICENSE
    
    if [ "$BRANCH" = "intelliJ-14" ]; then
        git commit -m "Add LICENSE - Rainbow Brackets Proprietary License from 2022.3 branch" || echo "Already committed"
    else
        git commit -m "Update LICENSE to Rainbow Brackets Proprietary License from 2022.3 branch" || echo "Already committed"
    fi
    
    # Push the feature branch
    echo "Pushing $FEATURE_BRANCH..."
    git push -u origin "$FEATURE_BRANCH"
    
    # Create PR using gh CLI
    echo "Creating PR for $BRANCH..."
    gh pr create \
        --base "$BRANCH" \
        --head "$FEATURE_BRANCH" \
        --title "Update LICENSE to Rainbow Brackets Proprietary License" \
        --body "This PR updates the LICENSE file to match the Rainbow Brackets Proprietary License from the 2022.3 branch.

**Changes:**
- Replaces the current license with the Rainbow Brackets Proprietary License
- Ensures consistency across all branches

**Original request:** 根据2022.3分支上的最新license把所有分支的license都更新一遍"
    
    echo "PR created for $BRANCH"
    echo ""
done

echo "========================================="
echo "All PRs created successfully!"
echo "========================================="
