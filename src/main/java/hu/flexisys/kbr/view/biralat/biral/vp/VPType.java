package hu.flexisys.kbr.view.biralat.biral.vp;

import java.util.ArrayList;
import java.util.List;

public enum VPType {
    VP_40(2), VP_41(3), VP_42(2), VP_53(5), VP_61(5), VP_63(4), VP_64(9);

    private Integer paramsNum;

    VPType(Integer paramsNum) {
        this.paramsNum = paramsNum;
    }

    public static VPType parseVPType(Integer vpCode) {
        switch (vpCode) {
            case 40:
                return VP_40;
            case 41:
                return VP_41;
            case 42:
                return VP_42;
            case 53:
                return VP_53;
            case 61:
                return VP_61;
            case 63:
                return VP_63;
            case 64:
                return VP_64;
            default:
                return null;
        }
    }

    public Integer vpKod() {
        switch (this) {
            case VP_40:
                return 40;
            case VP_41:
                return 41;
            case VP_42:
                return 42;
            case VP_53:
                return 53;
            case VP_61:
                return 61;
            case VP_63:
                return 63;
            case VP_64:
                return 64;
            default:
                return null;
        }
    }

    public Integer calcVp(List<Integer> rawParams) {
        if (!validParams(rawParams)) {
            return null;
        }

        List<Double> params = refineParams(rawParams);
        Double rawValue = evaluateParams(params);
        Integer value = classifyRawValue(rawValue);
        Integer strictValue = applyStrictParamCheck(value);
        return strictValue;
    }

    private Boolean validParams(List<Integer> params) {
        if (!paramsNum.equals(params.size())) {
            return false;
        }

        for (Integer param : params) {
            if (param == null) {
                return false;
            }
        }

        return true;
    }

    private List<Double> refineParams(List<Integer> rawParams) {
        List<Double> params = new ArrayList<Double>();
        switch (this) {
            case VP_40:
            case VP_41:
            case VP_42:
            case VP_53:
            case VP_61:
                for (Integer rawParam : rawParams) {
                    Double param = Double.valueOf(rawParam);
                    params.add(param);
                }
                break;
            case VP_63:
            case VP_64:
                String kod = String.valueOf(vpKod());
                List<String> szempontSzarmaztatasList = VPTypeUtil.getSzempontSzarmaztatasList(kod);
                for (int i = 0; i < szempontSzarmaztatasList.size(); i++) {
                    String paramKod = szempontSzarmaztatasList.get(i);
                    String paramName = VPTypeUtil.getParamNameByKod(paramKod);
                    Integer rawParam = rawParams.get(i);

                    Double convertedParam = VPTypeUtil.getConvertedParamByName(paramName, rawParam - 1);
                    Double statValue = VPTypeUtil.getStatValueByNameFromConvertedValue(paramName, convertedParam);
                    params.add(statValue);
                }
                break;
        }
        return params;
    }

    private Double IH, IE, FL, HO, CU, TM, BF, FM, FH, FS, TH, TR, TS, CA, SM, TF, EI, TC, BE, BA, BV, BH;

    private Double evaluateParams(List<Double> params) {
        Double value;
        int i = 0;
        switch (this) {
            case VP_40:
                IH = params.get(i++);
                IE = params.get(i++);
                value = (IH * 0.6) + (IE * 0.4);
                break;
            case VP_41:
                FL = params.get(i++);
                HO = params.get(i++);
                CU = params.get(i++);
                value = (FL * 0.33) + (HO * 0.33) + (CU * 0.33);
                break;
            case VP_42:
                TM = params.get(i++);
                BF = params.get(i++);
                value = (TM * 0.6) + (BF * 0.4);
                break;
            case VP_53:
                FM = params.get(i++);
                FH = params.get(i++);
                FS = params.get(i++);
                TH = params.get(i++);
                TR = params.get(i++);
                value = (FM * 0.4) + (FH * 0.15) + (FS * 0.15) + (TH * 0.15) + (TR * 0.15);
                break;
            case VP_61:
                FM = params.get(i++);
                TS = params.get(i++);
                FH = params.get(i++);
                FS = params.get(i++);
                TR = params.get(i++);
                value = 80.0 + ((((FM - 143.4) / 3.8) * 0.5) + (((TS - 85.1) / 3.4) * 0.083) + (((FH - 54.3) / 2.3) * 0.083) +
                        (((FS - 54.5) / 2.7) * 0.167) + (((TR - 79.8) / 3.0) * 0.167)) * 4.8;
                break;
            case VP_63:
                CU = params.get(i++);
                HO = params.get(i++);
                CA = params.get(i++);
                SM = params.get(i++);
                value = ((CU * 0.2) + (HO * 0.4) + (CA * 0.2) + (SM * 0.2));
                break;
            case VP_64:
                TM = params.get(i++);
                TF = params.get(i++);
                TH = params.get(i++);
                EI = params.get(i++);
                TC = params.get(i++);
                BE = params.get(i++);
                BA = params.get(i++);
                BV = params.get(i++);
                BH = params.get(i++);
                value = (TM * 0.24) + (TF * 0.13) + (TH * 0.06) + (EI * 0.14) + (TC * 0.06) +
                        (BE * 0.15) + (BA * 0.1) + (BV * 0.06) + (BH * 0.06);
                break;
            default:
                value = null;
        }
        return value;
    }

    private Integer classifyRawValue(Double rawValue) {
        Integer value;
        switch (this) {
            case VP_63:
                value = VPTypeUtil.getIntervalValueForFund(rawValue);
                break;
            case VP_64:
                value = VPTypeUtil.getIntervalValueForEuter(rawValue);
                break;
            default:
                value = (int) Math.round(rawValue);
                break;
        }
        return value;
    }

    private Integer applyStrictParamCheck(Integer value) {
        Integer strictValue = value;
        switch (this) {
            case VP_63:
                if (CU == 9 && strictValue > 83) { strictValue = 83; }
                if (HO == 7 && strictValue > 81) { strictValue = 81; }
                if (CU == 3 && strictValue > 78) { strictValue = 78; }
                if (CU == 2 && strictValue > 73) { strictValue = 73; }
                if (HO == 2 && strictValue > 73) { strictValue = 73; }
                if (HO == 8 && strictValue > 73) { strictValue = 73; }
                if (CU == 1 && strictValue > 68) { strictValue = 68; }
                if (HO == 9 && strictValue > 68) { strictValue = 68; }
                if (HO == 1 && strictValue > 68) { strictValue = 68; }
                break;
            case VP_64:
                if (BE == 9 && strictValue > 83) { strictValue = 83; }
                if (BA == 9 && strictValue > 83) { strictValue = 83; }
                if (TM == 3 && strictValue > 78) { strictValue = 78; }
                if (BE == 2 && strictValue > 78) { strictValue = 78; }
                if (BA == 2 && strictValue > 78) { strictValue = 78; }
                if (TM == 2 && strictValue > 73) { strictValue = 73; }
                if (BH == 1 && strictValue > 74) { strictValue = 74; }
                if (BH == 2 && strictValue > 77) { strictValue = 77; }
                if (BV == 1 && strictValue > 74) { strictValue = 74; }
                if (BV == 2 && strictValue > 77) { strictValue = 77; }
                if (BA == 1 && strictValue > 71) { strictValue = 71; }
                if (BE == 1 && strictValue > 71) { strictValue = 71; }
                if (TM == 1 && strictValue > 68) { strictValue = 68; }
                break;
            default:
                break;
        }
        return strictValue;
    }
}
