git clone https://github.com/hofaphil/sudoku-expert-generator.git app/src/main/cpp/temp
rm -rf app/src/main/cpp/generator
mkdir app/src/main/cpp/generator
mv app/src/main/cpp/temp/src/* app/src/main/cpp/generator
rm -rf app/src/main/cpp/temp
