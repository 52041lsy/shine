import onnx
from onnx import version_converter, helper
 
# Preprocessing: load the model to be converted.
model_path = "yolov5n.onnx"
original_model = onnx.load(model_path)
 
print(f"The model before conversion:\n{original_model.opset_import[0].version}")
 
# A full list of supported adapters can be found here:
# https://github.com/onnx/onnx/blob/main/onnx/version_converter.py#L21
# Apply the version conversion on the original model
converted_model = version_converter.convert_version(original_model, 15)
 
print(f"The model after conversion:\n{converted_model.opset_import[0].version}")
onnx.save_model(converted_model,"new——v5n.onnx")
