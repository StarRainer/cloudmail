<template> 
  <div>
    <el-upload action="http://cloudmall-project.oss-cn-beijing.aliyuncs.com" :data="dataObj" list-type="picture"
      :multiple="false" :show-file-list="showFileList" :file-list="fileList" :before-upload="beforeUpload"
      :on-remove="handleRemove" :on-success="handleUploadSuccess" :on-preview="handlePreview">
      <el-button size="small" type="primary">点击上传</el-button>
      <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过10MB</div>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="fileList[0].url" alt="">
    </el-dialog>
  </div>
</template>
<script>
import { policy } from './policy'
import { getUUID } from '@/utils'

export default {
  name: 'singleUpload',
  props: {
    value: String
  },
  computed: {
    imageUrl() {
      return this.value;
    },
    imageName() {
      if (this.value != null && this.value !== '') {
        return this.value.substr(this.value.lastIndexOf("/") + 1);
      } else {
        return null;
      }
    },
    fileList() {
      return [{
        name: this.imageName,
        url: this.imageUrl
      }]
    },
    showFileList: {
      get: function () {
        return this.value !== null && this.value !== '' && this.value !== undefined;
      },
      set: function (newValue) {
      }
    }
  },
  data() {
    return {
      dataObj: {
        policy: '',
        'x-oss-signature': '',
        key: '',
        dir: '',
        host: '',
        'x-oss-security-token': '',    // 对应 securityToken
        'x-oss-date': '',              // 对应 xOssDate
        'x-oss-credential': '',        // 对应 xOssCredential
        'x-oss-signature-version': 'OSS4-HMAC-SHA256', // 对应 xOssSignatureVersion
        'success_action_status': '200'
      },
      dialogVisible: false
    };
  },
  methods: {
    emitInput(val) {
      this.$emit('input', val)
    },
    handleRemove(file, fileList) {
      this.emitInput('');
    },
    handlePreview(file) {
      this.dialogVisible = true;
    },
    beforeUpload(file) {
      let _self = this;
      return new Promise((resolve, reject) => {
        policy().then(response => {
          const data = response.data;

          _self.dataObj.policy = data.policy;
          _self.dataObj['x-oss-signature'] = data.signature;
          _self.dataObj.dir = data.dir;
          let rawHost = data.host;
          if (rawHost.includes('sts')) {
            _self.dataObj.host = rawHost.replace('.sts.', '.oss-');
          } else {
            _self.dataObj.host = rawHost;
          }
          _self.dataObj['x-oss-security-token'] = data.securityToken;
          _self.dataObj['x-oss-credential'] = data.xossCredential;
          _self.dataObj['x-oss-date'] = data.xossDate;
          _self.dataObj['x-oss-signature-version'] = data.xossSignatureVersion;
          _self.dataObj.key = data.dir + getUUID() + '_${filename}';

          resolve(true);
        }).catch(err => {
          console.error("获取签名失败", err);
          reject(false);
        })
      })
    },
    handleUploadSuccess(res, file) {
      console.log("上传成功...");
      this.showFileList = true;
      this.fileList.pop();

      // 拼接真实访问地址
      // 注意：OSS 上传成功后默认返回 XML，不返回 JSON。res 可能是空的或者是 XML 解析结果
      // 图片的真实路径 = host + / + key (把 ${filename} 替换回真实文件名)
      const finalUrl = this.dataObj.host + '/' + this.dataObj.key.replace("${filename}", file.name);

      this.fileList.push({ name: file.name, url: finalUrl });
      this.emitInput(finalUrl);
    }
  }
}
</script>
<style></style>
