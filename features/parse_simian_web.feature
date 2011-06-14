Feature: PostSimianWeb
  In order to serve remote users
  As a code reviewer
  I want to post a simian report over http and have the parsed JSON come back to me

  Scenario: Post simian report
    Given a simian report:
    """
    <?xml version=\"1.0\" encoding=\"UTF-8\"?>
    <?xml-stylesheet href=\"simian.xsl\" type=\"text/xsl\"?>
    <!--Similarity Analyser 2.2.24 - http://www.redhillconsulting.com.au/products/simian/index.htmlCopyright (c) 2003-08 RedHill Consulting Pty. Ltd.  All rights reserved.Simian is not free unless used solely for non-commercial or evaluation purposes.-->
    <simian version=\"2.2.24\">
    <check ignoreCharacterCase=\"true\" ignoreCurlyBraces=\"true\" ignoreIdentifierCase=\"true\" ignoreModifiers=\"true\" ignoreStringCase=\"true\" threshold=\"6\">
      <set lineCount=\"6\">
        <block sourceFile=\"/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/beans/report/ReportTasksBean.java\" startLineNumber=\"333\" endLineNumber=\"340\"/>
        <block sourceFile=\"/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/beans/cct/CCTTasksBean.java\" startLineNumber=\"187\" endLineNumber=\"194\"/>
      </set>
      <set lineCount=\"7\"> 
        <block sourceFile=\"/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/lodge/LodgementUtilities.java\" startLineNumber=\"175\" endLineNumber=\"182\"/> 
        <block sourceFile=\"/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/lodge/rebuid/SystemRebuilderImpl.java\" startLineNumber=\"199\" endLineNumber=\"206\"/> 
        <block sourceFile=\"/Users/avombatk/projects/healthcheck/build/src/au/com/westpac/pda/beans/report/ReportTasksBean.java\" startLineNumber=\"333\" endLineNumber=\"340\"/> 
      </set>
      <summary duplicateFileCount=\"241\" duplicateLineCount=\"10208\" duplicateBlockCount=\"830\" totalFileCount=\"662\" totalRawLineCount=\"138208\" totalSignificantLineCount=\"60994\" processingTime=\"1173\"/>
    </check></simian>
    """
    When I post it to the application
    Then I get back a web page 
    And it contains a JSON graph
