<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <v-container fluid>
      <p>Type is {{ types || 'null' }}</p>
      <v-radio-group
          v-model="types"
          mandatory
      >
        <v-radio
            label="symbols"
            value="symbols"
        ></v-radio>
        <v-radio
            label="words"
            value="words"
        ></v-radio>
      </v-radio-group>
    </v-container>
    <v-container fluid>
      <p>Language is {{ lang || 'null' }}</p>
      <v-radio-group
          v-model="lang"
          mandatory
      >
        <v-radio
            label="English"
            value="EN"
        ></v-radio>
        <v-radio
            label="Russian"
            value="RU"
        ></v-radio>
      </v-radio-group>
    </v-container>
    <v-container fluid>
      <v-btn
          color="primary"
          class="text-none"
          round
          depressed
          :loading="isSelecting"
          @click="onButtonClick"
      >
        <v-icon left>mdi-cloud-upload</v-icon>
        testtest
      </v-btn>
      <input
          ref="uploader"
          class="d-none"
          type="file"
          @change="onFileChanged"
      >
    </v-container>
    <v-btn v-on:click="submit()">Submit</v-btn>
    <div class="random" v-if="loaded">
      <trend-chart
          :datasets="[
            {
              data: allValues.Cm,
              showPoints: true,
              smooth: true,
              fill: true,
              className: 'Cm'
            },
            {
              data: allValues.dCm,
              showPoints: true,
              smooth: true,
              fill: true,
              className: 'dCm'
            }

          ]"
          :grid="{
            verticalLines: true,
            horizontalLines: true
  }"
          :labels="{
            xLabels: allValues.m,
            yLabels: 5,
            yLabelsTextFormatter: val => Math.round(val * 100) / 100
      }"
          :min="0"
          :interactive="true" @mouse-move="onMouseMove">
      </trend-chart>
      <div id="pop" role="tooltip" ref="tooltip" class="tooltip" :class="{'is-active': tooltipData}">
        <div class="tooltip-container" v-if="tooltipData">
          <strong>{{labels.xLabels[tooltipData.index]}}</strong>
          <div class="tooltip-data">
            <div class="tooltip-data-item tooltip-data-item--1">{{tooltipData.data[0]}}</div>
            <div class="tooltip-data-item tooltip-data-item--2">{{tooltipData.data[1]}}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import Vuetify from 'vuetify'
import TrendChart from "vue-trend-chart";

Vue.use(TrendChart);
Vue.use(Vuetify);

export default {
  name: 'HelloWorld',
  props: {
    msg: String
  },
  vuetify: new Vuetify(),
  data() {
    return {
      isSelecting: false,
      selectedFile: null,
      types: null,
      lang: null,
      loaded: false,
      allValues: {
        Cm: [],
        dCm: [],
        m: []
      },
      n: null,
      mu: null,
      maxdCm: null,
      tooltipData: null,
      popper: null,
      popperIsActive: false
    }
  },
  methods: {
    onButtonClick() {
      this.isSelecting = true;
      window.addEventListener('focus', () => {
        this.isSelecting = false
      }, { once: true });

      this.$refs.uploader.click()
    },
    onFileChanged(e) {
      this.selectedFile = e.target.files[0];
    },
    onMouseMove(params) {
      console.log(params);
    },
    submit () {
      let self = this;
      self.loaded = false;
      self.allValues = {
        Cm: [],
        dCm: [],
        m: []
      }
      let url = new URL(`http://localhost:8081/api/estimates`);
      let dataFile = new FormData();
      dataFile.append('file', this.selectedFile);
      dataFile.append('lang', this.lang);
      dataFile.append('type', this.types)
      fetch(url, {
        method: 'POST',
        body: dataFile
      })
          .then(response => response.json())
          .then(data => {
            data.values.forEach(el => {
              self.allValues.Cm.push({value: el.Cm});
              self.allValues.dCm.push({value:el.dCm});
              self.allValues.m.push( String(el.m));
            });
            self.loaded = true;
          });
    }
  },
}
</script>

<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
strong {
  font-weight: 600;
}

body {
  padding: 0;
  margin: 0;
  font-family: "Source Sans Pro", sans-serif;
  color: #2f4053;
}
.random {
  width: 100%;
}
.labels {
  stroke: rgba(0, 0, 0, 0.05);
}
.active-line {
  stroke: rgba(0, 0, 0, 0.2);
}
.point {
  stroke-width: 2;
  transition: stroke-width 0.2s;
}
.point.is-active {
  stroke-width: 5;
}
</style>
