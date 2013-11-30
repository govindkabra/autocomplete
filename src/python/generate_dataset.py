import sys
from term_score_pb2 import TermScore, TermScoreDataset

"""
 This class helps generate the binary data required by java TermRanker server.
 It starts by reading a text file, constructs TermScoreDocument, then serializes it to file. 
 This file is passed in as paratmer to TermRanker when starting server. 
"""
class GenerateDataset:
  def run(self, txt_file, out_file):
    data = self.prepareDataset(txt_file)
    self.saveDataset(data, out_file)

  def prepareDataset(self, txt_file):
    dataset = TermScoreDataset()
    f = open(txt_file, 'r')
    txt = f.readlines() 
    f.close()
    # now read each line and create TermScore entries from it.
    for row in txt:
      row_arr = row.strip().split(" ")
      try:
        name = row_arr[0].decode("UTF-8").encode('ascii', 'ignore')
        ts = dataset.termScore.add()
        ts.name = name
        ts.score = int(row_arr[1])
      except UnicodeDecodeError as e:
        print e
  
    return dataset

  def saveDataset(self, dataset, out_file):
    f = open(out_file, 'w')
    f.write(dataset.SerializeToString())
    f.close()

  def readDataset(self, out_file):
    f = open(out_file, 'r')
    d = f.read()
    f.close()
    data = TermScoreDataset()
    data.ParseFromString(d)
    for dt in data.termScore:
      print dt.name, dt.score

if __name__ == "__main__":
  if (len(sys.argv) == 3):
    GenerateDataset().run(sys.argv[1], sys.argv[2])
  else:
    print "[USAGE]: python %s input_txt_file output_protobuf_bin_file" % (sys.argv[0])

