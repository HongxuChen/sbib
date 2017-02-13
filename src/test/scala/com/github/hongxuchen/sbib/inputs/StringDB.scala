package com.github.hongxuchen.sbib.inputs

object StringDB {

  val malformedFieldsDB =
    """
      |@inproceedings{Swamy:2006:MPU:1155442.1155678,
      | author = {Swamy, Nikhil and Hicks, Michael and Tse, Stephen and Zdancewic, Steve},
      | title = {Managing Policy Updates in Security-Typed Languages},
      | series = {CSFW '06},
      | year = {2006},
      | isbn = {0-7695-2615-2},
      | pages = {202--216},
      | numpages = {15},
      | url = {http://dx.doi.org/10.1109/CSFW.2006.17},
      | doi = {10.1109/CSFW.2006.17},
      | acmid = {1155678},
      | publisher = {IEEE Computer Society},
      | address = {Washington, DC, USA},
      |}
      |
      |@Inbook{vanRest2014,
      |author="Van Rest, Jeroen
      |and Van Rijn, Martin
      |and Van Paassen, Ron",
      |editor="P., Bart and Ikonomou, Demosthenes",
      |title="Designing Privacy-by-Design",
      |bookTitle="Privacy Technologies and Policy: First Annual Privacy Forum, APF 2012, Limassol, Cyprus, October 10-11, 2012, Revised Selected Papers",
      |year="2014",
      |address="Berlin, Heidelberg",
      |publisher="Springer Berlin Heidelberg",
      |pages="55--72",
      |}
      |
      |@article{Enck:2009:UAS:1512148.1512324,
      | author = {Enck, William and Ongtang, Machigar and McDaniel, Patrick},
      | title = {Understanding Android Security},
      | journal = {IEEE Security and Privacy},
      | issue_date = {January 2009},
      | volume = {7},
      | number = {1},
      | month = {jan},
      | year = {2009},
      | issn = {1540-7993},
      | pages = {50--57},
      | numpages = {8},
      | url = {http://dx.doi.org/10.1109/MSP.2009.26},
      | doi = {10.1109/MSP.2009.26},
      | acmid = {1512324},
      | publisher = {IEEE Educational Activities Department},
      | address = {Piscataway, NJ, USA},
      | keywords = {Android, Android, mobile phones, Smartphones, security, Smartphones, mobile phones, security},
      |}
    """.stripMargin

}
