<template>
  <v-app id="inspire">
    <div class="hello">
      <h1 class="text-center">{{msg}}</h1>
      <v-col style="display: flex; justify-content: space-between">
        <div>
          <v-container fluid :justify="'space-around'">
            <b>Тип обхода</b>
            <v-radio-group
                v-model="types"
                mandatory
            >
              <v-radio
                  label="по символам"
                  value="symbols"
              ></v-radio>
              <v-radio
                  label="по словам"
                  value="words"
              ></v-radio>
            </v-radio-group>
          </v-container>
          <v-container fluid :justify="'space-around'">
            <b>Язык</b>
            <v-radio-group
                v-model="lang"
                mandatory
            >
              <v-radio
                  label="english"
                  value="EN"
              ></v-radio>
              <v-radio
                  label="русский"
                  value="RU"
              ></v-radio>
            </v-radio-group>
          </v-container>
          <v-container fluid :justify="'space-around'">
            <v-btn
                color="primary"
                class="text-none"
                round
                depressed
                :loading="isSelecting"
                @click="onButtonClick"
            >
              <v-icon left>mdi-cloud-upload</v-icon>
              {{labelButton}}
            </v-btn>
            <input
                ref="uploader"
                class="d-none"
                type="file"
                @change="onFileChanged"
            >
          </v-container>
          <v-container><v-btn color="warning" v-on:click="submit()">Submit</v-btn></v-container>
        </div>
        <div v-if="loaded">
          <v-data-table
              dense
              :headers="headers"
              :items="allItems"
              item-key="name"
              class="elevation-1"
          ></v-data-table>
        </div>
      </v-col>
      <div class="random" v-if="loaded">
        <div class="text-center">
          <v-chip
                  class="ma-2"
                  color="#C8F89A"
          >
            Максимальная инверсная разность: {{maxdCm}}
          </v-chip>
          <v-chip
                  class="ma-2"
                  color="#C8F89A"
          >
            Мера символьного разнообразия: {{mu}}
          </v-chip>
          <v-chip
                  class="ma-2"
                  color="#C8F89A"
          >
            Количесвто элементов: {{n}}
          </v-chip>
        </div>
        <trend-chart
            :datasets="[
            {
              data: allValues.Cm,
              showPoints: true,
              smooth: false,
              className: 'curve1'
            },
            {
              data: allValues.dCm,
              showPoints: true,
              smooth: false,
              className: 'curve2'
            }
          ]"
            :grid="{
            verticalLines: true,
            horizontalLines: true
  }"
            :labels="{
            xLabels: allValues.m,
            yLabels: 11,
            yLabelsTextFormatter: val => val.toFixed(2)
      }"
            :min="0"
            :interactive="true" @mouse-move="onMouseMove" class="random-chart">
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
  </v-app>
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
      popperIsActive: false,
      indexValue: null,
      activeCm: null,
      activedCm: null,
      labelButton: 'Choose the file',
      headers: [
        {
          text: 'm',
          align: 'start',
          sortable: false,
          value: 'm',
        },
        {
          text: 'C(m)',
          value: 'Cm'
        },
        {
          text: 'dC(m)',
          value: 'dCm'
        }
      ],
      allItems: null,
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
      this.labelButton = this.selectedFile.name;
    },
    onMouseMove(params) {
      console.log(params);
      if (params.index === 0 || params.index) {
        this.indexValue = params.index + 1;
        this.activeCm = params.data[0].value;
        this.activedCm = params.data[1].value;
      }
    },
    submit () {
      let self = this;
      self.loaded = false;
      self.allValues = {
        Cm: [],
        dCm: [],
        m: []
      }
      self.allItems = [];
      this.labelButton = "Choose the file";
      let url = "/api/estimates";
      let dataFile = new FormData();
      dataFile.append('file', this.selectedFile);
      dataFile.append('lang', this.lang);
      dataFile.append('type', this.types);
      dataFile.append('maxWindowSize', 15);
      fetch(url, {
        method: 'POST',
        body: dataFile,
      })
          .then(response => response.json())
          .then(data => {
            self.maxdCm = data.maxdCm;
            self.n = data.n;
            self.mu = data.mu;
            data.values.forEach(el => {
              self.allValues.Cm.push({value: el.Cm});
              self.allValues.dCm.push({value:el.dCm});
              self.allValues.m.push(String(el.m));
              self.allItems.push({
                m: el.m,
                Cm: el.Cm !== undefined ? el.Cm.toFixed(3) : null,
                dCm: el.dCm !== undefined ? el.dCm.toFixed(3) : null
              });
            });
            self.loaded = true;
          });
    }
  },
}
</script>
<style lang="scss">
  .random-chart { width: 50%; }
  .random {
    width: 100%;
    text-align: center;
    .vtc {
      height: 250px;
      font-size: 12px;
      @media (min-width: 699px) {
        height: 320px;
      }
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
    .curve1 {
      .stroke {
        stroke: #e42020;
        stroke-width: 2;
      }
      .point {
        fill: #e42020;
        stroke: #e42020;
      }
    }
    .curve2 {
      .stroke {
        stroke: #177900;
        stroke-width: 2;
      }
      .point {
        fill: #177900;
        stroke: #177900;
      }
    }
    .tooltip {
      &:not(.is-active) {
        display: none;
      }
      padding: 10px;
      background: #fff;
      box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
      pointer-events: none;
      &-data {
        display: flex;
        &-item {
          display: flex;
          align-items: center;
          &:not(:first-child) {
            margin-left: 20px;
          }
          &:before {
            content: "";
            display: block;
            width: 15px;
            height: 15px;
            margin-right: 5px;
          }
          &--1:before {
            background: #fbac91;
          }
          &--2:before {
            background: #fbe1b6;
          }
          &--3:before {
            background: #7fdfd4;
          }
        }
      }
    }
  }
</style>
