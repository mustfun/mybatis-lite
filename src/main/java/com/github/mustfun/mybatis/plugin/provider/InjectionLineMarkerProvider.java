package com.github.mustfun.mybatis.plugin.provider;

import com.github.mustfun.mybatis.plugin.annotation.Annotation;
import com.github.mustfun.mybatis.plugin.dom.model.Mapper;
import com.github.mustfun.mybatis.plugin.setting.MybatisLiteSetting;
import com.github.mustfun.mybatis.plugin.util.Icons;
import com.github.mustfun.mybatis.plugin.util.JavaUtils;
import com.github.mustfun.mybatis.plugin.util.MapperUtils;
import com.github.mustfun.mybatis.plugin.util.MybatisConstants;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author yanglin
 * @update itar
 * @function 注入的一个lineMarkerProvider
 */
public class InjectionLineMarkerProvider extends RelatedItemLineMarkerProvider {

  @Override
  protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
    Map<String, String> valueMap = MybatisLiteSetting.getInstance().getValueMap();
    if (!MybatisConstants.TRUE.equalsIgnoreCase(valueMap.get(MybatisConstants.NAVIGATION_OPEN_STATUS))) {
      return;
    }

    if (!(element instanceof PsiField)) {
      return;
    }
    PsiField field = (PsiField) element;
    if (!isTargetField(field)) {
      return;
    }

    PsiType type = field.getType();
    if (!(type instanceof PsiClassReferenceType)) {
      return;
    }

    Optional<PsiClass> clazz = JavaUtils.findClazz(element.getProject(), type.getCanonicalText());
    if (!clazz.isPresent()) {
      return;
    }

    PsiClass psiClass = clazz.get();
    Optional<Mapper> mapper = MapperUtils.findFirstMapper(element.getProject(), psiClass);
    if (!mapper.isPresent()) {
      return;
    }

    NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(Icons.SPRING_INJECTION_ICON_NEW)
                    .setAlignment(GutterIconRenderer.Alignment.CENTER)
                    .setTarget(psiClass)
                    .setTooltipTitle("导航至文件 - " + psiClass.getQualifiedName());
    result.add(builder.createLineMarkerInfo(field.getNameIdentifier()));
  }

  /**
     * AUTOWIRED注解或者resource上的name和 filed的name一致
     * @param field
     * @return
     */
    private boolean isTargetField(PsiField field) {
        if (JavaUtils.isAnnotationPresent(field, Annotation.AUTOWIRED)) {
            return true;
        }
        Optional<PsiAnnotation> resourceAnno = JavaUtils.getPsiAnnotation(field, Annotation.RESOURCE);
        if (resourceAnno.isPresent()) {
            PsiAnnotationMemberValue nameValue = resourceAnno.get().findAttributeValue("name");
            String name = Objects.requireNonNull(nameValue).getText().replaceAll("\"", "");
            return StringUtils.isBlank(name) || name.equals(field.getName());
        }
        return false;
    }

}
