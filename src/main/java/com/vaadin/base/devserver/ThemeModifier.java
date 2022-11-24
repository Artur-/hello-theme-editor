package com.vaadin.base.devserver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.helger.commons.collection.impl.ICommonsList;
import com.helger.css.ECSSVersion;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpression;
import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.reader.CSSReader;
import com.helger.css.writer.CSSWriter;

public class ThemeModifier {

    public static void updateCssProperty(String property, String value) {
        File projectFolder = new File("."); // FIXME
        String themeName = "hello-theme-editor"; // FIXME

        File frontend = new File(projectFolder, "frontend");
        File themes = new File(frontend, "themes");
        File theme = new File(themes, themeName);
        File styles = new File(theme, "styles.css");
        CascadingStyleSheet styleSheet = CSSReader.readFromFile(styles, StandardCharsets.UTF_8, ECSSVersion.LATEST);
        CSSStyleRule htmlHostRule = findHtmlHostrule(styleSheet);

        CSSDeclaration declaration = htmlHostRule.getAllDeclarations()
                .findFirst(decl -> decl.getProperty().equals(property));
        CSSExpression expression = CSSExpression.createSimple(value);
        if (declaration == null) {
            declaration = new CSSDeclaration(property, expression);
            htmlHostRule.addDeclaration(declaration);
        } else {
            declaration.setExpression(expression);
        }
        try {
            new CSSWriter().setWriteHeaderText(false).writeCSS(styleSheet, new FileWriter(styles));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static CSSStyleRule findHtmlHostrule(CascadingStyleSheet csss) {
        ICommonsList<CSSStyleRule> allRules = csss.getAllStyleRules();
        CSSStyleRule rule = allRules.findFirst(r -> {
            if (r.getSelectorCount() == 2 && "html".equals(r.getSelectorAtIndex(0).getAsCSSString())
                    && ":host".equals(r.getSelectorAtIndex(1).getAsCSSString())) {
                return true;
            }
            return false;
        });

        if (rule == null) {
            rule = new CSSStyleRule();
            CSSSelector selector = new CSSSelector();
            CSSSelectorSimpleMember member = new CSSSelectorSimpleMember("html, :host");
            selector.addMember(member);
            rule.addSelector(selector);
            csss.addRule(rule);
        }

        return rule;
    }

}
