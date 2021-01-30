package com.github.izhangzhihao.rainbow.brackets.settings.form

import com.github.izhangzhihao.rainbow.brackets.RainbowHighlighter
import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.application.options.colors.*
import com.intellij.ide.util.PropertiesComponent
import com.intellij.ui.ColorPanel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.EventDispatcher
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode
import java.awt.Color
import java.awt.event.ActionListener
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel


class RainbowOptionsPanel(
        private val options: ColorAndFontOptions,
        private val schemesProvider: SchemesPanel,
        private val category: String
) : OptionsPanel {

    private lateinit var rootPanel: JPanel
    private lateinit var optionsTree: Tree

    private lateinit var rainbow: JBCheckBox

    private lateinit var colorLabel1: JLabel
    private lateinit var colorLabel2: JLabel
    private lateinit var colorLabel3: JLabel
    private lateinit var colorLabel4: JLabel
    private lateinit var colorLabel5: JLabel

    private val colorLabels: Array<JLabel>

    private lateinit var color1: ColorPanel
    private lateinit var color2: ColorPanel
    private lateinit var color3: ColorPanel
    private lateinit var color4: ColorPanel
    private lateinit var color5: ColorPanel

    private val colors: Array<ColorPanel>

    private lateinit var gradientLabel: JLabel

    private val properties: PropertiesComponent = PropertiesComponent.getInstance()
    private val eventDispatcher: EventDispatcher<ColorAndFontSettingsListener> =
            EventDispatcher.create(ColorAndFontSettingsListener::class.java)

    init {
        colors = arrayOf(color1, color2, color3, color4, color5)
        colorLabels = arrayOf(colorLabel1, colorLabel2, colorLabel3, colorLabel4, colorLabel5)

        val actionListener = ActionListener {
            eventDispatcher.multicaster.settingsChanged()
            options.stateChanged()
        }
        rainbow.addActionListener(actionListener)
        for (c in colors) {
            c.addActionListener(actionListener)
        }

        options.addListener(object : ColorAndFontSettingsListener.Abstract() {
            override fun settingsChanged() {
                if (!schemesProvider.areSchemesLoaded()) return
                if (optionsTree.selectedValue != null) {
                    // update options after global state change
                    processListValueChanged()
                }
            }
        })

        optionsTree.apply {
            isRootVisible = false
            model = DefaultTreeModel(DefaultMutableTreeTableNode())
            selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
            addTreeSelectionListener {
                if (schemesProvider.areSchemesLoaded()) {
                    processListValueChanged()
                }
            }
        }
    }

    override fun getPanel(): JPanel = rootPanel

    override fun addListener(listener: ColorAndFontSettingsListener) {
        eventDispatcher.addListener(listener)
    }

    override fun updateOptionsList() {
        fillOptionsList()
        processListValueChanged()
    }

    private data class DescriptionsNode(val rainbowName: String, val descriptions: List<TextAttributesDescription>) {
        override fun toString(): String = rainbowName
    }

    private fun fillOptionsList() {
        val nodes = options.currentDescriptions.asSequence()
                .filter { it is TextAttributesDescription && it.group == category }
                .map {
                    val description = it as TextAttributesDescription
                    val rainbowName = description.toString().split(":")[0]
                    rainbowName to description
                }
                .groupBy { it.first }
                .map { (rainbowName, descriptions) ->
                    DefaultMutableTreeNode(DescriptionsNode(rainbowName,
                            descriptions.asSequence().map { it.second }.toList().sortedBy { it.toString() }))
                }
        val root = DefaultMutableTreeNode()
        for (node in nodes) {
            root.add(node)
        }

        (optionsTree.model as DefaultTreeModel).setRoot(root)
    }

    private fun processListValueChanged() {
        var descriptionsNode = optionsTree.selectedDescriptions
        if (descriptionsNode == null) {
            properties.getValue(SELECTED_COLOR_OPTION_PROPERTY)?.let { preselected ->
                optionsTree.selectOptionByRainbowName(preselected)
                descriptionsNode = optionsTree.selectedDescriptions
            }
        }

        descriptionsNode?.run {
            properties.setValue(SELECTED_COLOR_OPTION_PROPERTY, rainbowName)
            reset(rainbowName, descriptions)
        } ?: resetDefault()
    }

    private fun resetDefault() {
        rainbow.isEnabled = false
        rainbow.isSelected = false
        gradientLabel.text = "Assign each brackets its own color from the spectrum below:"

        for (i in 0 until minRange()) {
            colors[i].isEnabled = false
            colors[i].selectedColor = null
            colorLabels[i].isEnabled = false
        }
    }

    private fun reset(rainbowName: String, descriptions: List<TextAttributesDescription>) {
        val rainbowOn = RainbowHighlighter.isRainbowEnabled(rainbowName)

        rainbow.isEnabled = true
        rainbow.isSelected = rainbowOn
        gradientLabel.text = "Assign each ${rainbowName.toLowerCase()} its own color from the spectrum below:"

        for (i in 0 until minRange()) {
            colors[i].isEnabled = rainbowOn
            colorLabels[i].isEnabled = rainbowOn
            colors[i].selectedColor = descriptions.indexOfOrNull(i)?.rainbowColor
            descriptions.indexOfOrNull(i)?.let { eventDispatcher.multicaster.selectedOptionChanged(it) }
        }
    }


    override fun applyChangesToScheme() {
        val scheme = options.selectedScheme
        val (rainbowName, descriptions) = optionsTree.selectedDescriptions ?: return
        when (rainbowName) {
            RainbowHighlighter.NAME_ROUND_BRACKETS,
            RainbowHighlighter.NAME_ANGLE_BRACKETS,
            RainbowHighlighter.NAME_SQUARE_BRACKETS,
            RainbowHighlighter.NAME_SQUIGGLY_BRACKETS -> {
                RainbowHighlighter.setRainbowEnabled(rainbowName, rainbow.isSelected)

                for (i in 0 until minRange()) {
                    colors[i].selectedColor?.let { color ->
                        descriptions[i].rainbowColor = color
                        descriptions[i].apply(scheme)
                    }
                }
            }
        }
    }

    private fun minRange() = minOf(RainbowSettings.instance.numberOfColors, 5)

    override fun processListOptions(): MutableSet<String> = mutableSetOf(
            RainbowHighlighter.NAME_ROUND_BRACKETS,
            RainbowHighlighter.NAME_SQUARE_BRACKETS,
            RainbowHighlighter.NAME_SQUIGGLY_BRACKETS,
            RainbowHighlighter.NAME_ANGLE_BRACKETS
    )

    override fun showOption(option: String): Runnable? = Runnable {
        optionsTree.selectOptionByRainbowName(option)
    }

    override fun selectOption(typeToSelect: String) {
        optionsTree.selectOptionByType(typeToSelect)
    }

    companion object {
        private const val SELECTED_COLOR_OPTION_PROPERTY = "rainbow.selected.color.option.name"

        private var TextAttributesDescription.rainbowColor: Color?
            get() = externalForeground
            set(value) {
                externalForeground = value
            }

        private val Tree.selectedValue: Any?
            get() = (lastSelectedPathComponent as? DefaultMutableTreeNode)?.userObject

        private val Tree.selectedDescriptions: DescriptionsNode?
            get() = selectedValue as? DescriptionsNode

        private fun Tree.findOption(nodeObject: Any, matcher: (Any) -> Boolean): TreePath? {
            val model = model as DefaultTreeModel
            for (i in 0 until model.getChildCount(nodeObject)) {
                val childObject = model.getChild(nodeObject, i)
                if (childObject is DefaultMutableTreeNode) {
                    val data = childObject.userObject
                    if (matcher(data)) {
                        return TreePath(model.getPathToRoot(childObject))
                    }
                }

                val pathInChild = findOption(childObject, matcher)
                if (pathInChild != null) return pathInChild
            }

            return null
        }

        private fun Tree.selectOptionByRainbowName(rainbowName: String) {
            selectPath(findOption(model.root) { data ->
                data is DescriptionsNode
                        && rainbowName.isNotBlank()
                        && data.rainbowName.contains(rainbowName, ignoreCase = true)
            })
        }

        private fun Tree.selectOptionByType(attributeType: String) {
            selectPath(findOption(model.root) { data ->
                data is DescriptionsNode && data.descriptions.any { it.type == attributeType }
            })
        }

        private fun Tree.selectPath(path: TreePath?) {
            if (path != null) {
                selectionPath = path
                scrollPathToVisible(path)
            }
        }
    }
}

private fun <E> List<E>.indexOfOrNull(idx: Int): E? = if (idx < this.size) this[idx] else null