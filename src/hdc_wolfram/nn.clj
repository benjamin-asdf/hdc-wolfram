(ns hdc-wolfram.nn
  (:require
   [wolframite.api.v1 :as wl]
   ;; [wolframite.lib.helpers :as h]
   [wolframite.runtime.defaults]
   [wolframite.tools.hiccup :as wh]
   [wolframite.wolfram :as w]
   ;; [scicloj.clay.v2.api :as clay]
   [wolframite.impl.wolfram-syms.intern :as wi]))


;; https://reference.wolfram.com/language/CUDALink/guide/CUDALink.html

;; (wl/! "Plus[1,2]")

(wl/! "Needs[\"CUDALink`\"]")

(wl/! "CUDAMap[Cos, RandomReal[1, 10]]")

(def CUDAMap (wi/wolfram-fn (quote CUDAMap)))

(wl/! (CUDAMap w/Cos (w/RandomReal 1 10)))
;; mat = CUDAMatrix[RandomReal[1, {5, 5}]]

(wl/! (list 'CUDAMap w/Cos (w/RandomReal 1 2)))
[0.6705818705558697 0.9839953780309295]


(wl/! "mat = CUDAMatrix[RandomReal[1, {5000, 5000}]];")
(wl/! "mat")

(def mat
  (wl/! (list 'CUDAMatrix (w/RandomReal 1 [5 5]))))




(wl/! "Needs[\"CUDALink`\"]")
nil

(wl/! (list 'CUDAInformation))
(wl/! (list 'CUDADriverVersion))
(wl/! (list '$CUDADeviceCount))
(wl/! (list '$CUDALinkPath))
(wl/! (list '$CUDADevice))



(wl/! "Needs[\"CUDALink`\"]")
(wl/! (list 'CUDAQ))
true
(wl/! "vec = CUDAVector[ {1., 2., 3.}];")
nil
(wl/! "CUDADot[vec,vec]")
14.0



(wl/! (list 'CUDAVector [1. 2. 3.]))

(wl/! (list 'CUDAVector [1. 2. 3.]))


(wl/! "mem1 = CUDAMemoryAllocate[Integer, {10, 10}];")
(wl/! "CUDAMemoryGet[mem1]")


(wl/! "CUDAMemoryInformation[mem1]")
(wl/! "CUDAMemoryCopyToHost[mem1]")

(wl/! "mem = CUDAMemoryLoad[{1, 2, 3}];")
(wl/! "CUDAMemoryInformation[mem]")
;; data transfer is only with this function:
(wl/! "CUDAMemoryCopyToDevice[mem]")

'(CUDAMemory 1566947825
             (-> "Type" "Integer64")
             (-> "Dimensions" [3])
             (-> "ByteCount" 24)
             (-> "Residence" "DeviceHost")
             (-> "Sharing" "Manual")
             (-> "Unique" true)
             (-> "Platform" 1)
             (-> "Device" 1)
             (-> "MathematicaType" List)
             (-> "TypeInformation" []))

;; device status:
;; Uninitialized -> Initialized -> Synchronized


(def firstkernel
  "__global__ void addTwo_kernel(mint * arry, mint len) {

    int index = threadIdx.x + blockIdx * blockDim.x;

    if (index >= len) return;

    arry[index] += 2;
}")



(wl/! "secondKernelCode = \"
__device__ float f(float x) {
	return tanf(x);
}
__global__ void secondKernel(float * diff, float h, mint listSize) {
	int index = threadIdx.x + blockIdx.x * blockDim.x;
    float f_n = f(((float) index) / h);
    float f_n1 = f((index + 1.0) / h);
	if( index < listSize) {
		diff[index] = (f_n1 - f_n) / h;
	}
}\";")

(wl/! "secondKernel =
 CUDAFunctionLoad[secondKernelCode,
  \"secondKernel\", {{\"Float\"}, \"Float\", _Integer}, 16]")

(wl/! "buffer = CUDAMemoryAllocate[\"Float\", 1024]")
(wl/! "secondKernel[buffer, 100.0, 1024]")

(wl/! "CUDAMemoryGet[buffer][[;; 30]]")

;; [0.0 3.65582230783781E233 3.634548747348817E185 5.354650906140881E102
;;  1.021234236187575E277 5.285555291328858E180 1.0445738905987918E-142
;;  5.5644045291552226E141 2.1177615885462676E161 5.709514350156802E228
;;  6.077807943557383E180 6.013470016991697E-154 7.675749669851524E170
;;  4.6539911074403354E151 6.9605557256330044E252 6.205847970291951E228
;;  2.662122982329011E98 6.013469530600097E-154 6.013470016999068E-154
;;  6.013470016999068E-154 6.013470016999068E-154 6.013470016999068E-154
;;  6.013470016999068E-154 3.890283325512953E-80 2.255230917442187E-80
;;  8.548499285625071E-72 3.50896887653056E151 1.2764014980111379E-52
;;  1.9625324614918448E243 6.186184045445028E223]

;; (wh/view "ListPlot@CUDAMemoryGet[buffer]")

(comment
  (wl/start!)
  (wl/stop!))
