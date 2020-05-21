window.onload = function(){
let xuehao = [26, 15, 34, 36, 38, 16, 19, 24, 25, 37];
let xianshi = this.document.getElementById("xianshi");
let span = this.document.getElementById("span");
let state = 0;
let time;
let gailu = 0;
let hh;
let ui = 0;
span.onclick = function (){
    if (state === 0){
       clearInterval(time);
       time = setInterval(function () {
        let hh = Math.round(Math.random() * xuehao.length - 1);
          xianshi.innerHTML = xuehao[hh]
          console.log(xuehao[hh]);
          console.log(hh)
      }, 1);
      state = 1
}else{
  state=0;
  clearInterval(time);
}
};
// setInterval(function () {
//   // for(let i = 0 ; i < 1600000 ; i++){
//     hh = Math.round(Math.random() * xuehao.length - 1);
//     console.log(xuehao[hh]);
//     if(hh === -0 || hh === 0 || hh === xuehao.length - 1 ||hh === -1){
//       gailu ++;
//       xianshi.innerHTML = gailu;
//       ui = gailu / 1600000
//       span.innerHTML = ui;
//     }
//     console.log(hh)

//   // }
//  }, 1000);
}
