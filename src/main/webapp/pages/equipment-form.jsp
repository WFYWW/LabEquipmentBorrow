<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>设备信息 - 校园实验设备借用系统</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<jsp:include page="/includes/header.jsp"/>

<div class="container container-narrow">
    <div class="page-title">
        <h1><s:if test="equipment.id > 0">✏️ 编辑设备</s:if><s:else>＋ 新增设备</s:else></h1>
        <a href="equipment-list.action" class="btn btn-text">← 返回列表</a>
    </div>

    <s:form action="equipment-save" theme="simple" cssClass="form-card" enctype="multipart/form-data">
        <s:hidden name="equipment.id"/>

        <div class="form-row">
            <label>设备图片</label>
            <div class="form-img-preview">
                <img src="<s:property value='equipment.imageDisplay'/>" alt="设备图片预览">
            </div>
            <s:file name="upload" cssClass="input" accept="image/*"/>
            <div class="form-hint">上传新图（jpg / png / gif / webp，不超过 5MB）；也可从下方预设图选择；两者都留空则保持当前图片</div>
            <s:fielderror fieldName="upload"/>
        </div>
        <div class="form-row">
            <label>或选择预设图片</label>
            <s:select name="equipment.image" list="presetImages" headerKey=""
                      headerValue="— 无图片（显示默认占位图）—" cssClass="input"/>
        </div>

        <div class="form-row">
            <label>设备名称 <span class="req">*</span></label>
            <s:textfield name="equipment.name" cssClass="input" placeholder="如：数字示波器"/>
            <s:fielderror fieldName="equipment.name"/>
        </div>
        <div class="form-row">
            <label>规格型号</label>
            <s:textfield name="equipment.model" cssClass="input" placeholder="如：RIGOL DS1054Z"/>
        </div>
        <div class="form-row">
            <label>存放位置</label>
            <s:textfield name="equipment.location" cssClass="input" placeholder="如：实验楼A-301"/>
        </div>
        <div class="form-row">
            <label>设备状态</label>
            <s:select name="equipment.status" list="{'在库','借出','维修','报废'}" cssClass="input"/>
        </div>
        <div class="form-row">
            <label>备注说明</label>
            <s:textarea name="equipment.description" rows="3" cssClass="input"
                        placeholder="选填，如配件、注意事项等"/>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">保　存</button>
            <a href="equipment-list.action" class="btn btn-text">取消</a>
        </div>
    </s:form>
</div>

<jsp:include page="/includes/footer.jsp"/>
</body>
</html>
