package org.springai.multimodeldemo.data;

public enum ModelEnum {

    // 视觉理解
    VISUAL_COMPREHENSION("qwen-vl-max"),
    // 视觉推理
    VISUAL_REASONING("qvq-max"),
    // 文字提取
    EXTRACT_TEXT("qwen-vl-ocr"),
    // 音频理解
    AUDIO_COMPREHENSION("qwen-audio-turbo"),
    // 全模态
    FULL_MODALITY("qwen-omni-turbo");

    private final String model;

    // 构造函数，注意构造函数必须是private，防止外部创建枚举实例
    ModelEnum(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }
}
