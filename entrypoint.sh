#!/bin/bash
/opt/ort/bin/ort analyze --input-dir /home/ort --output-dir /home/ort/results
/opt/ort/bin/ort scan -i /home/ort/results/analyzer-result.yml -o /home/ort/results
#/opt/ort/bin/ort advise -a OSV --analyzer-result /home/ort/results/analyzer-result.yml --output-dir /home/ort/results
#/opt/ort/bin/ort evaluate --license-classifications-file /home/ort/.ort/config/license-classifications.yml --package-curations-file /home/ort/.ort/config/curations.yml --rules-file /home/ort/.ort/config/evaluator.rules.kts -i /home/ort/results/scan-result.yml -o /home/ort/results
/opt/ort/bin/ort report -f PlainTextTemplate,StaticHtml,WebApp -i /home/ort/results/scan-result.yml -o /home/ort/results
