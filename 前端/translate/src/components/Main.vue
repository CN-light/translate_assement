<template>
  <el-container class="all">
    <el-header>{{title}}</el-header>

    <el-main>
      <div class="select-div">
        <el-select v-model="translate.engine" class="select">
          <el-option
            v-for="item in engineOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
        <el-select v-model="translate.source" class="select">
          <el-option
            v-for="item in languageOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
        <el-select v-model="translate.target" class="select">
          <el-option
            v-for="item in languageOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </div>
      <div class="textarea-div">
        <el-input
          type="textarea"
          :rows="10"
          resize="none"
          :placeholder="placeholder.text"
          v-model="translate.input"
          class="input"
        ></el-input>
        <el-input
          type="textarea"
          :rows="10"
          resize="none"
          v-model="translate.output"
          class="output"
          readonly
        ></el-input>
      </div>
      <div class="grade-div">
        <div class="inline-grade-div">
          <el-input
            v-model.number="translate.grade"
            :placeholder="placeholder.grade"
            clearable
            @blur="check"
          ></el-input>
          <span class="validate">{{validate}}</span>
        </div>
      </div>
      <div class="options-div">
        <div class="inline-options-div">
          <el-button type="primary" plain class="button" @click="exportAsExcel">{{button.export}}</el-button>
          <el-button type="primary" class="button" @click="submit">{{button.submit}}</el-button>
        </div>
      </div>
    </el-main>
    <el-footer>© {{year}} {{writer}}</el-footer>
  </el-container>
</template>

<style scope>
@import "../assets/css/main.css";
</style>

<script>
import utils from "../assets/js/utils.js";
export default {
  name: "Main",
  data() {
    return {
      title: "",
      year: "",
      writer: "",
      engineOptions: [],
      languageOptions: [],
      placeholder: {},
      button: {},
      validateOptions: [],
      notify: {},

      validate: "",
      inputTemp: "",
      translate: {
        engine: "youdao",
        source: "chinese",
        target: "english",
        input: "",
        output: "",
        grade: "",
        ip: ""
      }
    };
  },
  methods: {
    clean() {
      this.translate.grade = "";
    },
    exportAsExcel() {
      fetch("/translate/exportAsExcel", {
        method: "POST",
        headers: { "content-Type": "application/json;charset=utf-8;" },
        body: JSON.stringify({
          ip: this.getIp()
        })
      })
        .then(res => {
          return res.json();
        })
        .then(data => {
          var title = [
            "翻译引擎",
            "源语言",
            "目标语言",
            "原文",
            "译文",
            "分数"
          ];
          var json = [];
          for (var i = 0; i < data.length; i++) {
            var ijson = [];
            ijson.push(data[i].engine == undefined ? "" : data[i].engine);
            ijson.push(data[i].source == undefined ? "" : data[i].source);
            ijson.push(data[i].target == undefined ? "" : data[i].target);
            ijson.push(
              data[i].sourceText == undefined ? "" : data[i].sourceText
            );
            ijson.push(
              data[i].targetText == undefined ? "" : data[i].targetText
            );
            ijson.push(data[i].grade == undefined ? "" : data[i].grade);
            json.push(ijson);
          }
          utils.exportAsExcel(json, title);
        })
        .catch(err => {
          this.$notify.error({
            title: this.notify.title.fail,
            message: this.notify.message[2] + err
          });
        });
    },
    check() {
      this.validate = "";
      if (this.translate.grade == "") {
        this.validate = this.validateOptions[0];
        return false;
      } else if (typeof this.translate.grade != "number") {
        this.validate = this.validateOptions[1];
        return false;
      } else if (this.translate.grade < 0) {
        this.validate = this.validateOptions[2];
        return false;
      } else if (this.translate.grade > 100) {
        this.validate = this.validateOptions[3];
        return false;
      } else if (this.translate.input == "") {
        this.validate = this.validateOptions[4];
        return false;
      } else if (this.translate.output == "") {
        this.validate = this.validateOptions[5];
        return false;
      } else {
        return true;
      }
    },
    submit() {
      if (this.check() == true) {
        fetch("/translate/submit", {
          method: "POST",
          headers: { "content-Type": "application/json;charset=utf-8;" },
          body: JSON.stringify({
            engine: this.translate.engine,
            source: this.translate.source,
            target: this.translate.target,
            sourceText: this.translate.input,
            targetText: this.translate.output,
            grade: this.translate.grade,
            ip: this.getIp()
          })
        })
          .then(res => {
            return res.json();
          })
          .then(data => {
            if (data.result == "success") {
              this.$notify({
                title: this.notify.title.success,
                message: this.notify.message[0],
                type: "success"
              });
              this.clean();
            } else {
              this.$notify.error({
                title: this.notify.title.fail,
                message: this.notify.message[1]
              });
            }
          })
          .catch(err => {
            this.$notify.error({
              title: this.notify.title.fail,
              message: this.notify.message[2] + err
            });
          });
      }
    },
    getIp() {
      return returnCitySN["cip"];
    },
    sendTranslate() {
      if (this.translate.input.trim() != "") {
        fetch("/translate/translate", {
          method: "POST",
          headers: { "content-Type": "application/json;charset=utf-8;" },
          body: JSON.stringify({
            source: this.translate.source,
            target: this.translate.target,
            text: this.translate.input,
            engine: this.translate.engine
          })
        })
          .then(res => {
            return res.json();
          })
          .then(data => {
            this.translate.output = data.translation;
            if (data.error != undefined) {
              this.$notify.error({
                title: this.notify.title.fail,
                message: this.notify.message[3] + data.error
              });
            }
          })
          .catch(err => {
            this.$notify.error({
              title: this.notify.title.fail,
              message: this.notify.message[2] + err
            });
          });
      }
    }
  },
  watch: {
    "translate.engine": {
      handler(newValue, oldValue) {
        this.sendTranslate();
      }
    },
    "translate.source": {
      handler(newValue, oldValue) {
        if (newValue == this.translate.source) {
          for (let i = 0; i < this.languageOptions.length; i++) {
            if (this.languageOptions[i].value == newValue) {
              this.translate.target = this.languageOptions[
                (i + 1) % this.languageOptions.length
              ].value;
              break;
            }
          }
        }
        this.sendTranslate();
      }
    },
    "translate.target": {
      handler(newValue, oldValue) {
        if (newValue == this.translate.source) {
          for (let i = 0; i < this.languageOptions.length; i++) {
            if (this.languageOptions[i].value == newValue) {
              this.translate.source = this.languageOptions[
                (i + 1) % this.languageOptions.length
              ].value;
              break;
            }
          }
        }
        this.sendTranslate();
      },
      deep: true
    },
    "translate.input": {
      handler(newValue, oldValue) {
        this.inputTemp = newValue;
        //避免每改动一个字符都需要向服务器请求，对input建立一个缓存，
        //若1s之后的缓存值和实际值相等，则向服务器请求
        setTimeout(() => {
          if (this.inputTemp == newValue) {
            this.sendTranslate();
          }
        }, 1000);
      }
    }
  },
  created: function() {
    fetch("../../static/data.json")
      .then(res => {
        return res.json();
      })
      .then(res => {
        this.title = res.zh.title;
        this.year = res.zh.year;
        this.writer = res.zh.writer;
        this.engineOptions = res.zh.options.engineOptions;
        this.languageOptions = res.zh.options.languageOptions;
        this.placeholder = res.zh.placeholder;
        this.button = res.zh.button;
        this.validateOptions = res.zh.options.validateOptions;
        this.notify = res.zh.notify;
      });
  },
  mounted: function() {
    const s = document.createElement("script");
    s.type = "text/javascript";
    s.src = "http://pv.sohu.com/cityjson?ie=utf-8";
    document.body.appendChild(s);
  }
};
</script>