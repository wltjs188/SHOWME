# Copyright 2017 The TensorFlow Authors. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ==============================================================================




from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import argparse

import numpy as np
import requests
import tensorflow as tf

import pymysql.cursors






def get_pattern_name(pattern):
  return{
    'check' : '체크무늬',
    'dots' : '도트무늬',
    'flower' : '꽃무늬',
    'stripes' : '줄무늬',
    'tigger' : '호피무늬',
    }.get(pattern,1)

def load_graph(model_file):
  graph = tf.Graph()
  graph_def = tf.GraphDef()

  with open(model_file, "rb") as f:
    graph_def.ParseFromString(f.read())
  with graph.as_default():
    tf.import_graph_def(graph_def)

  return graph

def read_tensor_from_image_url(url,
                               input_height=299,
                               input_width=299,
                               input_mean=0,
                               input_std=255):
  image_reader = tf.image.decode_jpeg(
    requests.get(url).content, channels=3, name="jpeg_reader")
  float_caster = tf.cast(image_reader, tf.float32)
  dims_expander = tf.expand_dims(float_caster, 0)
  resized = tf.image.resize_bilinear(dims_expander, [input_height, input_width])
  normalized = tf.divide(tf.subtract(resized, [input_mean]), [input_std])

  with tf.Session() as sess:
    return sess.run(normalized)


def read_tensor_from_image_file(file_name,
                                input_height=299,
                                input_width=299,
                                input_mean=0,
                                input_std=255):
  input_name = "file_reader"
  output_name = "normalized"
  file_reader = tf.read_file(file_name, input_name)
  if file_name.endswith(".png"):
    image_reader = tf.image.decode_png(
        file_reader, channels=3, name="png_reader")
  elif file_name.endswith(".gif"):
    image_reader = tf.squeeze(
        tf.image.decode_gif(file_reader, name="gif_reader"))
  elif file_name.endswith(".bmp"):
    image_reader = tf.image.decode_bmp(file_reader, name="bmp_reader")
  else:
    image_reader = tf.image.decode_jpeg(
        file_reader, channels=3, name="jpeg_reader")
  float_caster = tf.cast(image_reader, tf.float32)
  dims_expander = tf.expand_dims(float_caster, 0)
  resized = tf.image.resize_bilinear(dims_expander, [input_height, input_width])
  normalized = tf.divide(tf.subtract(resized, [input_mean]), [input_std])
  sess = tf.Session()
  result = sess.run(normalized)

  return result


def load_labels(label_file):
  label = []
  proto_as_ascii_lines = tf.gfile.GFile(label_file).readlines()
  for l in proto_as_ascii_lines:
    label.append(l.rstrip())
  return label


if __name__ == "__main__":

  conn = pymysql.connect(host='18.191.10.193',
        user='kimcheon',
        password='kim2cheon1',
        db='SHOWOOMI',
        charset='utf8')
  curs = conn.cursor(pymysql.cursors.DictCursor)
  sql = "SELECT productId,optionNum,image FROM Product" # 실행 할 쿼리문 입력
  curs.execute(sql) # 쿼리문 실행
  rows = curs.fetchall() # 데이터 패치

#  try:    
#  finally:
    

  
  model_file = \
    "../pattern/output_graph.pb"
  label_file = "../pattern/output_labels.txt"
  input_height = 299
  input_width = 299
  input_mean = 0
  input_std = 255
  input_layer = "Placeholder"
  output_layer = "final_result"

  parser = argparse.ArgumentParser()
  parser.add_argument("--image", help="image to be processed")
  parser.add_argument("--graph", help="graph/model to be executed")
  parser.add_argument("--labels", help="name of file containing labels")
  parser.add_argument("--input_height", type=int, help="input height")
  parser.add_argument("--input_width", type=int, help="input width")
  parser.add_argument("--input_mean", type=int, help="input mean")
  parser.add_argument("--input_std", type=int, help="input std")
  parser.add_argument("--input_layer", help="name of input layer")
  parser.add_argument("--output_layer", help="name of output layer")
  args = parser.parse_args()

  if args.graph:
    model_file = args.graph
  if args.image:
    file_name = args.image
  if args.labels:
    label_file = args.labels
  if args.input_height:
    input_height = args.input_height
  if args.input_width:
    input_width = args.input_width
  if args.input_mean:
    input_mean = args.input_mean
  if args.input_std:
    input_std = args.input_std
  if args.input_layer:
    input_layer = args.input_layer
  if args.output_layer:
    output_layer = args.output_layer

  graph = load_graph(model_file)

  count=208;
  for row in rows[209:]:
    
    url = row['image']  #이미지 url  이거사용하면됨
    url=url.replace("90x90","400x400") #이미지크기 수정
    if not 'gif' in url :
      count+=1;
      print(count)
      print(url)
      t = read_tensor_from_image_url(
        url,
        input_height=input_height,
        input_width=input_width,
        input_mean=input_mean,
        input_std=input_std)
      input_name = "import/" + input_layer
      output_name = "import/" + output_layer
      input_operation = graph.get_operation_by_name(input_name)
      output_operation = graph.get_operation_by_name(output_name)

    
    #텐서플로우 실행
      with tf.Session(graph=graph) as sess:
        results = sess.run(output_operation.outputs[0], {
          input_operation.outputs[0]: t
          })
        results = np.squeeze(results)
        top_k = results.argsort()[-5:][::-1]
        labels = load_labels(label_file)
        i=top_k[0] #정확도 가장 높은 인덱스
        print("이미지:"+url)
        print("패턴", "정확도")
        print(labels[i], results[i] ,"%") #labels: 패턴결과값, results : 정확도
        if results[i]>=0.85:
          print('정확도높음')
          row['pattern']=get_pattern_name(labels[i])
          print(row['pattern'])
          sql = 'UPDATE Product SET pattern = %s WHERE productId = %s AND optionNum= %s'
          print(sql)
          curs.execute(sql,(row['pattern'],row['productId'],row['optionNum']))
          conn.commit()
          print(curs.rowcount)
          print("DB update완료")
                
        else:
          print('정확도낮음')
          sql = 'UPDATE Product SET pattern = NULL WHERE productId = %s AND optionNum= %s'
          print(sql)
          curs.execute(sql,(row['productId'],row['optionNum']))
          conn.commit()
          print(curs.rowcount)
          print("DB update완료")
        print("*****************************************")
      


        
conn.close()

